package com.auth.app.domain.model;

import java.time.OffsetDateTime;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogModel {
    private UUID id;
    private UUID userId;
    private String action;
    private String details;
    private String ipAddress;
    private OffsetDateTime createdAt;
}
