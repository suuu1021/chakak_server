package com.green.chakak.chakak._global.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NoSensitiveInfoValidator implements ConstraintValidator <NoSensitiveInfo, String> {

    private static final Pattern PATTERN = Pattern.compile(
            "(\\d{2,3}-\\d{3,4}-\\d{4})"
    );


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;
        return !PATTERN.matcher(value).find();
    }
}
