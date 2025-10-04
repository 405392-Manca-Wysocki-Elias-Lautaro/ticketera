package com.notification.app.services;

import java.util.Map;

public interface TemplateService {
    public String render(String templateName, Map<String, Object> variables);
}
