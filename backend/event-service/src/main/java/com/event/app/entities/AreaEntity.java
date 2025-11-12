package com.event.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "areas", schema="events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "is_general_admission", nullable = false)
    private Boolean isGeneralAdmission;

    @Column
    private Integer capacity;

    @Column
    private Integer position;
}

