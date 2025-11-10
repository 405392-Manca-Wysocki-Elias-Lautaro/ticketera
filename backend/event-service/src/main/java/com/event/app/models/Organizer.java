package com.event.app.models;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organizer {

    private UUID id;

    private String name;

    private String slug;

    private String contactEmail;

    private String phoneNumber;

    private LocalDateTime createdAt;

    private Boolean active;
    
}
