package com.notification.app.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.notification.app.services.TemplateService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    @Override
    public String render(String templateName, Map<String, Object> variables) {
        log.info("Template Service");
        try (
            
            InputStream inputStream = new ClassPathResource("templates/" + templateName).getInputStream()) {
            String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            for (var entry : variables.entrySet()) {
                html = html.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
            }

            return html;
        } catch (IOException e) {
            log.error("❌ Error loading email template '{}': {}", templateName, e.getMessage());

            throw new RuntimeException("Error loading email template", e);
        }
    }
}
