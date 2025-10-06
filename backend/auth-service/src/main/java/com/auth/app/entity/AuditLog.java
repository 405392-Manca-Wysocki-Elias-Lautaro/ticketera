package com.auth.app.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // puede ser null (ON DELETE SET NULL)

    @Column(nullable = false)
    private String action;

    @Column
    private String description;

    @Column(columnDefinition = "ip_address")
    private String ipAddress;
    
    @Column(columnDefinition = "user_agent")
    private String userAgent;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime createdAt;

        @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
