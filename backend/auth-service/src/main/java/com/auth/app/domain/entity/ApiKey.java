package com.auth.app.domain.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "api_keys",
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_api_keys_org_name", columnNames = {"organizer_id", "name"})
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;

    @Column(nullable = false)
    private String name;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @OneToMany(mappedBy = "apiKey", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ApiKeyScope> scopes;

    @Builder.Default
    @Column(nullable = false)
    private boolean revoked = false;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;

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
