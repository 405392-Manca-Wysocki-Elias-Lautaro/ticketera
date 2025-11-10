package com.auth.app.domain.entity;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;
import com.auth.app.utils.IpAddressConverter;
import com.auth.app.utils.UserAgentConverter;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trusted_devices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustedDevice {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_id", nullable = false, unique = true)
    private UUID deviceId;

    @Column(name = "ip_address")
    @Convert(converter = IpAddressConverter.class)
    private IpAddress ipAddress;

    @Column(name = "user_agent")
    @Convert(converter = UserAgentConverter.class)
    private UserAgent userAgent;

    private String location;

    @Column(name = "trusted", nullable = false)
    @Builder.Default
    private boolean trusted = true;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void delete() {
        this.deletedAt = OffsetDateTime.now();
    }

    public void restore() {
        this.deletedAt = null;
    }
}
