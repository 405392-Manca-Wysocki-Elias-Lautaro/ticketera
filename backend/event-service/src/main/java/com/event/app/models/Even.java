package com.event.app.models;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Even {

    private UUID id;

    private UUID organizerId;

    private String title;

    private String slug;

    private String description;

    private UUID categoryId;

    private String coverUrl;

    private String status;

    private LocalDateTime createdAt;

    private Boolean active;
}
