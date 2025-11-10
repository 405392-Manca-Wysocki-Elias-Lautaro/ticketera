package com.auth.app.dto.response;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private int status;
    private String code;
    private String message;
    private Map<String, String> details;
}
