package com.nter.insurance.mapper;

import com.nter.insurance.dto.ClientRequest;
import com.nter.insurance.dto.ClientResponse;
import com.nter.insurance.entity.ClientJPA;
import com.nter.insurance.utils.DniUtils;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    private final PolicyMapper policyMapper;

    public ClientMapper(PolicyMapper policyMapper) {
        this.policyMapper = policyMapper;
    }

    public ClientJPA toEntity(ClientRequest request) {
        return ClientJPA.builder()
                .idClient(request.idClient())
                .dni(DniUtils.normalize(request.dni()))
                .name(request.name().trim())
                .surname(request.surname().trim())
                .birthdate(request.birthdate())
                .build();
    }

    public ClientResponse toResponse(ClientJPA client) {
        return new ClientResponse(
                client.getIdClient(),
                client.getDni(),
                client.getName(),
                client.getSurname(),
                client.getBirthdate(),
                client.getPolicies().stream().map(policyMapper::toResponse).toList());
    }
}
