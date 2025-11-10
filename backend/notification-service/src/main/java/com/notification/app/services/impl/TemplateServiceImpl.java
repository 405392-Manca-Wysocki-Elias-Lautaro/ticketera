package com.notification.app.services.impl;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.notification.app.exceptions.custom.TemplateRenderException;
import com.notification.app.services.TemplateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    private final TemplateEngine templateEngine;

    @Override
    public String render(String templateName, Map<String, Object> variables) {
        try {
            log.info("🧩 Rendering template: {}", templateName);
            Context context = new Context();
            context.setVariables(variables);

            String cleanName = templateName.replace(".html", "");

            return templateEngine.process(cleanName, context);
        } catch (Exception e) {
            log.error("❌ Error rendering Thymeleaf template '{}': {}", templateName, e.getMessage(), e);
            throw new TemplateRenderException(e);
        }
    }
}
