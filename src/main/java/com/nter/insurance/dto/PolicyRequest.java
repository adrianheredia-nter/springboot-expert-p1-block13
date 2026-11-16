package com.nter.insurance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PolicyRequest(
        @NotBlank @Size(max = 50) String numPolicy,
        @NotBlank @Size(max = 100) String branch,
        @Size(max = 100) String subBranch,
        @NotNull Integer idClient) {
}
