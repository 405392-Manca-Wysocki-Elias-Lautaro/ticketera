package com.notification.app.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.notification.app.services.TemplateService;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Override
    public String render(String templateName, Map<String, Object> variables) {
        try {
            String html = Files.readString(Path.of("src/main/resources/templates/" + templateName));
            for (var entry : variables.entrySet()) {
                html = html.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
            }
            return html;
        } catch (IOException e) {
            throw new RuntimeException("Error loading email template", e);
        }
    }
}

