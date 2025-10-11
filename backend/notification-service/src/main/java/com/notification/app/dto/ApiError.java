package com.notification.app.dto;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@Builder
public class ApiError {
    private OffsetDateTime timestamp;
    private int status;
    private String errorCode;
    private String message;
    private String path;
}
