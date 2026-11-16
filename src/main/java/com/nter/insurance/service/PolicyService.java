package com.nter.insurance.service;

import com.nter.insurance.dto.PolicyRequest;
import com.nter.insurance.dto.PolicyResponse;

import java.util.List;

public interface PolicyService {

    List<PolicyResponse> findAll();

    PolicyResponse findById(Integer idPolicy);

    List<PolicyResponse> findByClient(Integer idClient);

    PolicyResponse create(PolicyRequest request);

    void delete(Integer idPolicy);
}
