package com.auth.app.exception.exceptions;

import com.auth.app.exception.ErrorCatalog;
import com.auth.app.exception.ErrorCodes;

import lombok.Getter;

@Getter
public class MissingOrganizationException extends RuntimeException {

    private final ErrorCatalog error;

    public MissingOrganizationException() {
        super("Current user does not have an organization assigned");
        this.error = ErrorCatalog.BAD_REQUEST;
    }
}
