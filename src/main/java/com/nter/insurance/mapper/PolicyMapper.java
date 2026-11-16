package com.nter.insurance.mapper;

import com.nter.insurance.dto.PolicyRequest;
import com.nter.insurance.dto.PolicyResponse;
import com.nter.insurance.entity.ClientJPA;
import com.nter.insurance.entity.PolicyJPA;
import org.springframework.stereotype.Component;

@Component
public class PolicyMapper {

    public PolicyJPA toEntity(PolicyRequest request, ClientJPA client) {
        return PolicyJPA.builder()
                .numPolicy(request.numPolicy().trim())
                .branch(request.branch().trim())
                .subBranch(request.subBranch())
                .client(client)
                .build();
    }

    public PolicyResponse toResponse(PolicyJPA policy) {
        return new PolicyResponse(
                policy.getIdPolicy(),
                policy.getNumPolicy(),
                policy.getBranch(),
                policy.getSubBranch(),
                policy.getClient().getIdClient());
    }
}
