package com.nter.insurance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PolicyRequest(
        @NotBlank String numPolicy,
        @NotBlank String branch,
        String subBranch,
        @NotNull Integer idClient) {
}
