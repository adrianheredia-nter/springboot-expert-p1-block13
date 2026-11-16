package com.nter.insurance.service.impl;

import com.nter.insurance.constants.AppConstants;
import com.nter.insurance.dto.PolicyRequest;
import com.nter.insurance.dto.PolicyResponse;
import com.nter.insurance.entity.ClientJPA;
import com.nter.insurance.entity.PolicyJPA;
import com.nter.insurance.exception.AlreadyExistsException;
import com.nter.insurance.exception.NotFoundException;
import com.nter.insurance.mapper.PolicyMapper;
import com.nter.insurance.repository.ClientRepository;
import com.nter.insurance.repository.PolicyRepository;
import com.nter.insurance.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final ClientRepository clientRepository;
    private final PolicyMapper policyMapper;

    @Override
    public List<PolicyResponse> findAll() {
        return policyRepository.findAll().stream().map(policyMapper::toResponse).toList();
    }

    @Override
    public PolicyResponse findById(Integer idPolicy) {
        return policyMapper.toResponse(getPolicy(idPolicy));
    }

    @Override
    public List<PolicyResponse> findByClient(Integer idClient) {
        getClient(idClient);
        return policyRepository.findByClientIdClient(idClient).stream().map(policyMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public PolicyResponse create(PolicyRequest request) {
        String numPolicy = request.numPolicy().trim();
        if (policyRepository.existsByNumPolicy(numPolicy)) {
            throw new AlreadyExistsException(AppConstants.POLICY_ALREADY_EXISTS.formatted(numPolicy));
        }
        ClientJPA client = getClient(request.idClient());
        PolicyJPA policy = policyMapper.toEntity(request, client);
        client.getPolicies().add(policy);
        return policyMapper.toResponse(policyRepository.save(policy));
    }

    @Override
    @Transactional
    public void delete(Integer idPolicy) {
        PolicyJPA policy = getPolicy(idPolicy);
        policy.getClient().getPolicies().remove(policy);
        policyRepository.delete(policy);
    }

    private PolicyJPA getPolicy(Integer idPolicy) {
        return policyRepository.findById(idPolicy)
                .orElseThrow(() -> new NotFoundException(AppConstants.POLICY_NOT_FOUND.formatted(idPolicy)));
    }

    private ClientJPA getClient(Integer idClient) {
        return clientRepository.findById(idClient)
                .orElseThrow(() -> new NotFoundException(AppConstants.CLIENT_NOT_FOUND.formatted(idClient)));
    }
}
