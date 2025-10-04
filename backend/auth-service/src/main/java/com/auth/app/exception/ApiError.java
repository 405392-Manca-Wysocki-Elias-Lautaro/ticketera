package com.auth.app.exception;

import java.time.OffsetDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private OffsetDateTime timestamp;
    private int status;
    private String errorCode;
    private String message;
    private String path; // endpoint que falló
    private Map<String, String> details; // para validaciones campo->mensaje
}

