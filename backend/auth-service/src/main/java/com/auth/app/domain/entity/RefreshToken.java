package com.auth.app.domain.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;
import com.auth.app.utils.IpAddressConverter;
import com.auth.app.utils.UserAgentConverter;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 🔹 Nuevo campo
    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "ip_address")
    @Convert(converter = IpAddressConverter.class)
    private IpAddress ipAddress;

    @Column(name = "user_agent")
    @Convert(converter = UserAgentConverter.class)
    private UserAgent userAgent;

    @Builder.Default
    private boolean remembered = false;

    @Builder.Default
    private boolean revoked = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    public void revoke() {
        this.revoked = true;
    }
}
