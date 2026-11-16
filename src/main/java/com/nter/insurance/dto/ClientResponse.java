package com.nter.insurance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nter.insurance.constants.AppConstants;

import java.time.LocalDate;
import java.util.List;

public record ClientResponse(
        Integer idClient,
        String dni,
        String name,
        String surname,
        @JsonFormat(pattern = AppConstants.DATE_PATTERN) LocalDate birthdate,
        List<PolicyResponse> policies) {
}
