package com.ticket.app.jobs;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.app.clients.NotificationClient;
import com.ticket.app.clients.NotificationClient.GenericNotificationDTO;
import com.ticket.app.clients.SystemEventClient;
import com.ticket.app.clients.SystemEventClient.EventSummaryDTO;
import com.ticket.app.clients.UserClient;
import com.ticket.app.clients.UserClient.UserResponse;
import com.ticket.app.domain.entities.NotificationLog;
import com.ticket.app.domain.entities.Ticket;
import com.ticket.app.repositories.NotificationLogRepository;
import com.ticket.app.repositories.TicketRepository;
import com.ticket.app.repositories.UserSettingsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventReminderJob {

    private final SystemEventClient eventClient;
    private final TicketRepository ticketRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationClient notificationClient;
    private final UserClient userClient;
    private final ObjectMapper objectMapper;

    // Check every 15 minutes by default
    @Scheduled(fixedRateString = "${reminders.check-rate:900000}")
    public void execute() {
        log.info("Starting EventReminderJob...");
        triggerReminders(LocalDateTime.now());
        log.info("EventReminderJob finished.");
    }

    /**
     * Public method to allow manual triggering from DebugController.
     */
    @Transactional
    public void triggerReminders(LocalDateTime refTime) {
        log.info("Triggering reminders for reference time: {}", refTime);

        // 1. Fetch upcoming events (look ahead 25 hours to cover the 24h reminders)
        LocalDateTime endRange = refTime.plusHours(25);
        List<EventSummaryDTO> upcomingEvents = fetchUpcomingEvents(refTime, endRange);
        log.info("Found {} upcoming events in range", upcomingEvents.size());

        // 2. Process each event
        for (EventSummaryDTO event : upcomingEvents) {
            processEventReminders(event, refTime);
        }
    }

    private List<EventSummaryDTO> fetchUpcomingEvents(LocalDateTime start, LocalDateTime end) {
        try {
            return eventClient.getUpcomingEvents(start, end);
        } catch (Exception e) {
            log.error("Failed to fetch upcoming events", e);
            return Collections.emptyList();
        }
    }

    private void processEventReminders(EventSummaryDTO event, LocalDateTime refTime) {
        // Find all valid tickets for this event
        List<Ticket> tickets = ticketRepository.findByEventId(event.getId());
        
        for (Ticket ticket : tickets) {
            // Get user preferences (or defaults)
            List<Integer> offsetMinutesList = getUserReminderOffsets(ticket.getUserId());
            
            for (Integer offsetMinutes : offsetMinutesList) {
                checkAndSendReminder(ticket, event, offsetMinutes, refTime);
            }
        }
    }

    private List<Integer> getUserReminderOffsets(UUID userId) {
        // Default: 24h (1440m) and 1h (60m)
        List<Integer> defaults = List.of(1440, 60);

        return userSettingsRepository.findByUserIdAndSettingKey(userId, "EVENT_REMINDER_OFFSETS")
                .map(settings -> {
                    try {
                        return objectMapper.readValue(settings.getSettingValue(), new TypeReference<List<Integer>>() {});
                    } catch (Exception e) {
                        log.error("Error parsing reminder settings for user {}", userId, e);
                        return defaults;
                    }
                })
                .orElse(defaults);
    }

    private void checkAndSendReminder(Ticket ticket, EventSummaryDTO event, Integer offsetMinutes, LocalDateTime refTime) {
        LocalDateTime eventStart = event.getStartsAt();
        LocalDateTime targetReminderTime = eventStart.minusMinutes(offsetMinutes);

        // Calculate difference in minutes between NOW (refTime) and TARGET
        // We want to send if refTime is >= targetReminderTime, but not too late (e.g., within 20 mins)
        long diffMinutes = Duration.between(targetReminderTime, refTime).toMinutes();

        // If we are within the window [0, 20] minutes AFTER the target time
        if (diffMinutes >= 0 && diffMinutes <= 20) {
            String logType = "EVENT_REMINDER_" + offsetMinutes + "M";

            // Check if already sent
            if (!notificationLogRepository.existsByTicketIdAndReminderType(ticket.getId(), logType)) {
                sendEmail(ticket, event, offsetMinutes);
                
                // Log it to prevent duplicate
                NotificationLog logEntry = NotificationLog.builder()
                        .ticketId(ticket.getId())
                        .reminderType(logType)
                        .status("SENT")
                        .sentAt(refTime.atOffset(java.time.ZoneOffset.UTC)) 
                        .build();
                notificationLogRepository.save(logEntry);
            }
        }
    }

    private void sendEmail(Ticket ticket, EventSummaryDTO event, Integer offsetMinutes) {
        // Resolve user email
        UserResponse user = null;
        try {
            user = userClient.getUserById(ticket.getUserId());
        } catch (Exception e) {
            log.warn("Failed to fetch user details for userId: {}", ticket.getUserId());
        }

        if (user == null || user.getEmail() == null) {
            log.warn("Cannot send reminder for ticket {}: User email not found", ticket.getId());
            return;
        }

        String subject = "Recordatorio: " + event.getTitle() + " comienza pronto";
        String templateName = "event-reminder";
        
        String timeString = (offsetMinutes >= 60) 
            ? (offsetMinutes / 60) + " horas" 
            : offsetMinutes + " minutos";

        GenericNotificationDTO notification = GenericNotificationDTO.builder()
                .channel("EMAIL")
                .type("EVENT_REMINDER")
                .to(user.getEmail())
                .subject(subject)
                .template(templateName)
                .variables(Map.of(
                        "eventName", event.getTitle(),
                        "venueName", event.getVenueName() != null ? event.getVenueName() : "TBA",
                        "startsAt", event.getStartsAt().toString(),
                        "timeLeft", timeString,
                        "ticketCode", ticket.getCode(),
                        "firstName", user.getFirstName() != null ? user.getFirstName() : "Usuario"
                ))
                .build();

        notificationClient.sendNotification(notification);
        log.info("Sent reminder email to {} for event {}", user.getEmail(), event.getId());
    }
}
