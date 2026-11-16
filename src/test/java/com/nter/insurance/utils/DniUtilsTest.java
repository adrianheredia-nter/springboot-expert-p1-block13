package com.nter.insurance.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DniUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"12345678Z", "12345678z", " 87654321-X "})
    void acceptsValidDni(String dni) {
        assertThat(DniUtils.isValid(dni)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"12345678A", "1234567Z", "ABCDEFGHZ"})
    void rejectsInvalidDni(String dni) {
        assertThat(DniUtils.isValid(dni)).isFalse();
    }

    @ParameterizedTest
    @CsvSource({"' 11111111-h ', 11111111H", "22222222J, 22222222J"})
    void normalizesDni(String input, String expected) {
        assertThat(DniUtils.normalize(input)).isEqualTo(expected);
    }
}
