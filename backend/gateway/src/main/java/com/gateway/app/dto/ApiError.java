package com.gateway.app.dto;

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
