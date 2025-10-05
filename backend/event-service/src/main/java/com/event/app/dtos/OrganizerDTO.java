package com.event.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerDTO {

    private String id;

    private String name;

    private String slug;

    private String contactEmail;

    private String phoneNumber;

}
