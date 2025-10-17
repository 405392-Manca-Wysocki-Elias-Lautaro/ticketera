package com.auth.app.utils;

import com.auth.app.domain.valueObjects.UserAgent;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserAgentConverter implements AttributeConverter<UserAgent, String> {

    @Override
    public String convertToDatabaseColumn(UserAgent attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public UserAgent convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new UserAgent(dbData);
    }
}
