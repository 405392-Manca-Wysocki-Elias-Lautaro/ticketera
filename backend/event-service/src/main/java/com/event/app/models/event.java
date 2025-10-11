package com.event.app.models;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private Long id;

    private Long organizerId;

    private String title;

    private String slug;

    private String description;

    private Long categoryId;

    private String coverUrl;

    private String status;

    private LocalDateTime createdAt;

    private Boolean active;
}
