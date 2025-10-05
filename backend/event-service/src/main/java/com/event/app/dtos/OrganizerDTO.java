package com.event.app.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerDTO {

    private String name;

    private String slug;

    private String contactEmail;

    private String phoneNumber;

    private LocalDateTime createdAt;
}
