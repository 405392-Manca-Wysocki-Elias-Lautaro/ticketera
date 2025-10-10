package com.notification.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRequest {
    private String firstName;
    private String lastName;
    private String to;
    private String token;
    private String link;
}
