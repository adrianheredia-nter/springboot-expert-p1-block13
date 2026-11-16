package com.nter.insurance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nter.insurance.constants.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ClientRequest(
        @NotNull @Positive Integer idClient,
        @NotBlank String dni,
        @NotBlank String name,
        @NotBlank String surname,
        @Past @JsonFormat(pattern = AppConstants.DATE_PATTERN) LocalDate birthdate) {
}
