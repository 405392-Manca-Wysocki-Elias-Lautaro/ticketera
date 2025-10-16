package com.auth.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        try {
            Method getPassword = obj.getClass().getMethod("getPassword");
            Method getConfirmPassword = obj.getClass().getMethod("getConfirmPassword");

            String password = (String) getPassword.invoke(obj);
            String confirmPassword = (String) getConfirmPassword.invoke(obj);

            if (password == null || confirmPassword == null) {
                return false;
            }

            boolean matches = password.equals(confirmPassword);
            if (!matches) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Passwords do not match")
                        .addPropertyNode("confirmPassword")
                        .addConstraintViolation();
            }
            return matches;

        } catch (Exception e) {
            // Si el DTO no tiene los métodos esperados, la validación falla
            return false;
        }
    }
}
