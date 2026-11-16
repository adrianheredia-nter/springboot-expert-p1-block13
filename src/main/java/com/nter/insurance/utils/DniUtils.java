package com.nter.insurance.utils;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Spanish DNI helpers. The control letter is the remainder of the number divided by 23.
 */
public final class DniUtils {

    private static final String CONTROL_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";
    private static final Pattern DNI_PATTERN = Pattern.compile("\\d{8}[A-Z]");

    private DniUtils() {
    }

    public static String normalize(String dni) {
        return dni == null ? null : dni.trim().replace("-", "").toUpperCase(Locale.ROOT);
    }

    public static boolean isValid(String dni) {
        String value = normalize(dni);
        if (value == null || !DNI_PATTERN.matcher(value).matches()) {
            return false;
        }
        int number = Integer.parseInt(value.substring(0, 8));
        return value.charAt(8) == CONTROL_LETTERS.charAt(number % 23);
    }
}
