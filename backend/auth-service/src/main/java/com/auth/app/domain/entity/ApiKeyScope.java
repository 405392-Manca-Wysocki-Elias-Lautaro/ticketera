package com.auth.app.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "api_key_scopes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyScope {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_key_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_api_key_scope_api_key"))
    private ApiKey apiKey;

    @Column(name = "scope", nullable = false)
    private String scope;
}
