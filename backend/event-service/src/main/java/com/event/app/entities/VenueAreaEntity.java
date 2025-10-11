package com.event.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "venue_areas", schema="events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueAreaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "is_general_admission", nullable = false)
    private Boolean isGeneralAdmission;

    @Column
    private Integer capacity;

    @Column
    private Integer position;
}

