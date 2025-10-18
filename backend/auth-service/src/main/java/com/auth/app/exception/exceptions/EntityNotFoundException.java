package com.auth.app.exception.exceptions;

import com.auth.app.exception.ErrorCatalog;

public class EntityNotFoundException extends RuntimeException {
    private final String entityName;
    private final String field;
    private final Object value;
    private final ErrorCatalog error;

    public EntityNotFoundException(Class<?> entityClass, String field, Object value) {
        super(buildMessage(entityClass.getSimpleName(), field, value));
        this.entityName = entityClass.getSimpleName();
        this.field = field;
        this.value = value;
        this.error = ErrorCatalog.ENTITY_NOT_FOUND;
    }

    public EntityNotFoundException(String entityName, String field, Object value) {
        super(buildMessage(entityName, field, value));
        this.entityName = entityName;
        this.field = field;
        this.value = value;
        this.error = ErrorCatalog.ENTITY_NOT_FOUND;
    }

    private static String buildMessage(String entity, String field, Object value) {
        return entity + " with " + field + " '" + value + "' not found";
    }

    public String getEntityName() {
        return entityName;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public ErrorCatalog getError() {
        return error;
    }
}

