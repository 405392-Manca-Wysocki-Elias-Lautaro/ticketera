package com.auth.app.security;

import java.util.Set;
import java.util.regex.Pattern;

import com.auth.app.exception.exceptions.WeakPasswordException;

public class PasswordValidator {

    // Lista negra simple, en un caso real podrías cargar desde archivo/dataset externo
    private static final Set<String> BLACKLIST = Set.of(
            "123456", "password", "qwerty", "111111", "abc123", "123123"
    );

    // Al menos 8 caracteres, una mayúscula, una minúscula, un número y un caracter especial
    private static final Pattern STRONG_PASSWORD_REGEX = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    public static void validate(String password) {
        if (BLACKLIST.contains(password.toLowerCase())) {
            throw new WeakPasswordException("PASSWORD_NOT_STRONG");
        }
        if (!STRONG_PASSWORD_REGEX.matcher(password).matches()) {
            throw new WeakPasswordException("PASSWORD_NOT_STRONG");
        }
    }
}
