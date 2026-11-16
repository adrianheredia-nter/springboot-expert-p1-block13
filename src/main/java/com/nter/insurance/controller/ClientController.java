package com.nter.insurance.controller;

import com.nter.insurance.constants.AppConstants;
import com.nter.insurance.dto.ClientRequest;
import com.nter.insurance.dto.ClientResponse;
import com.nter.insurance.dto.PolicyResponse;
import com.nter.insurance.service.ClientService;
import com.nter.insurance.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppConstants.CLIENTS_PATH)
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final PolicyService policyService;

    @GetMapping
    public List<ClientResponse> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/{idClient}")
    public ClientResponse findById(@PathVariable Integer idClient) {
        return clientService.findById(idClient);
    }

    @GetMapping("/{idClient}/policies")
    public List<PolicyResponse> findPolicies(@PathVariable Integer idClient) {
        return policyService.findByClient(idClient);
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(request));
    }

    @PutMapping("/{idClient}")
    public ClientResponse update(@PathVariable Integer idClient, @Valid @RequestBody ClientRequest request) {
        return clientService.update(idClient, request);
    }

    @DeleteMapping("/{idClient}")
    public ResponseEntity<Void> delete(@PathVariable Integer idClient) {
        clientService.delete(idClient);
        return ResponseEntity.noContent().build();
    }
}
