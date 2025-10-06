package com.auth.app.notification.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO implements Serializable {
    private String channel;
    private String type;
    private String recipient;
    private Map<String, Object> data;
}
