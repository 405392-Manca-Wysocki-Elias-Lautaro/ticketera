package com.ticket.app.controllers;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ticket.app.jobs.EventReminderJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/debug/reminders")
@RequiredArgsConstructor
@Profile("dev") // Only in dev
@Slf4j
public class ReminderDebugController {

    private final EventReminderJob eventReminderJob;

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerReminders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime simulatedTime) {
        
        LocalDateTime refTime = simulatedTime != null ? simulatedTime : LocalDateTime.now();
        log.info("Manually triggering EventReminderJob with simulated time: {}", refTime);
        
        eventReminderJob.triggerReminders(refTime);
        
        return ResponseEntity.ok("Reminders triggered for time: " + refTime);
    }
}
