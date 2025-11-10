package com.auth.app.security;

import java.util.Set;
import java.util.regex.Pattern;

import com.auth.app.exception.exceptions.CommonPasswordException;
import com.auth.app.exception.exceptions.WeakPasswordException;

public final class PasswordValidator {

    private PasswordValidator() {
    }

    // 🧱 Lista negra simple (puede venir de un dataset externo)
    private static final Set<String> BLACKLIST = Set.of(
            "123456", "password", "qwerty", "111111", "abc123", "123123",
            "000000", "letmein", "admin", "welcome", "test123"
    );

    // 🔐 Patrón fuerte:
    // Al menos 8 caracteres, una mayúscula, una minúscula, un número y un caracter especial
    private static final Pattern STRONG_PASSWORD_REGEX = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,64}$"
    );

    /**
     * Valida la seguridad de la contraseña según criterios de fuerza, longitud
     * y blacklist de contraseñas comunes.
     *
     * @param password contraseña a validar
     * @param email (opcional) correo del usuario para evitar similitudes
     */
    public static void validate(String password, String email) {
        if (password == null || password.isBlank()) {
            throw new WeakPasswordException("Password cannot be empty.");
        }

        // 🚫 Common passwords
        if (BLACKLIST.contains(password.toLowerCase())) {
            throw new CommonPasswordException();
        }

        // 🚫 Length (already enforced by regex, but explicit is clearer)
        if (password.length() < 8 || password.length() > 64) {
            throw new WeakPasswordException("Password must be between 8 and 64 characters long.");
        }

        // 🚫 Minimum complexity
        if (!STRONG_PASSWORD_REGEX.matcher(password).matches()) {
            throw new WeakPasswordException("Password does not meet the required security complexity.");
        }

        // 🚫 Avoid including part of the email address
        if (email != null) {
            String localPart = email.split("@")[0].toLowerCase();
            if (password.toLowerCase().contains(localPart)) {
                throw new WeakPasswordException("Password must not contain part of your email address.");
            }
        }
    }

    /**
     * Sobrecarga simple cuando no se dispone de email.
     */
    public static void validate(String password) {
        validate(password, null);
    }
}
