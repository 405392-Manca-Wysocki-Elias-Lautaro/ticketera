package com.event.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "seats", schema="events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "area_id", nullable = false)
    private UUID areaId;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "row_number", nullable = false)
    private Integer rowNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String label;
}

