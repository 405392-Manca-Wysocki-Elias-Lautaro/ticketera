package com.event.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "venue_seats", schema="events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueSeatEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "venue_area_id", nullable = false)
    private UUID venueAreaId;

    @Column(name = "seat_label", nullable = false, columnDefinition = "TEXT")
    private String seatLabel;

    @Column(name = "row_label", columnDefinition = "TEXT")
    private String rowLabel;

    @Column(name = "number_label", columnDefinition = "TEXT")
    private String numberLabel;
}

