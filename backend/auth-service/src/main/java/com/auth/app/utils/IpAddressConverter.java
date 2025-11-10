package com.auth.app.utils;

import com.auth.app.domain.valueObjects.IpAddress;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class IpAddressConverter implements AttributeConverter<IpAddress, String> {
    @Override
    public String convertToDatabaseColumn(IpAddress attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public IpAddress convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new IpAddress(dbData);
    }
}

