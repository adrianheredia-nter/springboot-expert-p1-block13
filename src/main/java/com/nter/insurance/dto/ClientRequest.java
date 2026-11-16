package com.nter.insurance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nter.insurance.constants.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ClientRequest(
        @NotNull @Positive Integer idClient,
        @NotBlank @Size(max = 12) String dni,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String surname,
        @Past @JsonFormat(pattern = AppConstants.DATE_PATTERN) LocalDate birthdate) {
}
