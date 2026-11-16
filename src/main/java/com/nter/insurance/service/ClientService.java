package com.nter.insurance.service;

import com.nter.insurance.dto.ClientRequest;
import com.nter.insurance.dto.ClientResponse;

import java.util.List;

public interface ClientService {

    List<ClientResponse> findAll();

    ClientResponse findById(Integer idClient);

    ClientResponse create(ClientRequest request);

    ClientResponse update(Integer idClient, ClientRequest request);

    void delete(Integer idClient);
}
