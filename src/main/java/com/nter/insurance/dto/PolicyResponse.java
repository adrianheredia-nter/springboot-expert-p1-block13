package com.nter.insurance.dto;

public record PolicyResponse(
        Integer idPolicy,
        String numPolicy,
        String branch,
        String subBranch,
        Integer idClient) {
}
