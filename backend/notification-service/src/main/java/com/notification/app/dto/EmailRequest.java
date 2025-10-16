package com.notification.app.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class EmailRequest {

    @NonNull
    private String to;
    private String firstName;
    private String lastName;
    private String link;
    private String ipAddress;
    private String userAgent;
    private String timestamp;          // Formateado (ej. "15/10/2025 18:30:00")
    private Integer expirationMinutes;  // Número, no string
    private String supportEmail;
}
