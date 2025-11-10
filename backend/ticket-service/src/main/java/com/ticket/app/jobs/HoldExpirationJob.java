package com.ticket.app.jobs;

import com.ticket.app.services.HoldService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HoldExpirationJob {

    private final HoldService holdService;

    public HoldExpirationJob(HoldService holdService) {
        this.holdService = holdService;
    }

    // 🕒 Ejecuta cada minuto
    @Scheduled(fixedRate = 300000)
    public void expireOldHolds() {
        holdService.expireHolds();
    }
}
