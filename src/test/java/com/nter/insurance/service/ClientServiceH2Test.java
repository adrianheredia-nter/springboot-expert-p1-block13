package com.nter.insurance.service;

import com.nter.insurance.dto.ClientRequest;
import com.nter.insurance.dto.ClientResponse;
import com.nter.insurance.dto.PolicyRequest;
import com.nter.insurance.dto.PolicyResponse;
import com.nter.insurance.entity.ClientJPA;
import com.nter.insurance.exception.AlreadyExistsException;
import com.nter.insurance.exception.NotFoundException;
import com.nter.insurance.mapper.ClientMapper;
import com.nter.insurance.mapper.PolicyMapper;
import com.nter.insurance.repository.ClientRepository;
import com.nter.insurance.repository.PolicyRepository;
import com.nter.insurance.service.impl.ClientServiceImpl;
import com.nter.insurance.service.impl.PolicyServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Runs the real services against the in-memory H2 database, seeded with data.sql.
 */
@DataJpaTest
@Import({ClientServiceImpl.class, PolicyServiceImpl.class, ClientMapper.class, PolicyMapper.class})
class ClientServiceH2Test {

    @Autowired
    private ClientService clientService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByIdLoadsSeededClientWithPolicies() {
        ClientResponse client = clientService.findById(1);

        assertThat(client.dni()).isEqualTo("12345678Z");
        assertThat(client.name()).isEqualTo("Laura");
        assertThat(client.birthdate()).isEqualTo(LocalDate.of(1988, 4, 12));
        assertThat(client.policies())
                .extracting(PolicyResponse::numPolicy)
                .containsExactlyInAnyOrder("POL-0001", "POL-0002");
    }

    @Test
    void createPersistsClientAndPolicy() {
        clientService.create(new ClientRequest(10, "11111111h", "Ana", "Lopez", LocalDate.of(1992, 1, 15)));
        PolicyResponse policy = policyService.create(new PolicyRequest("POL-1000", "car", "third party", 10));
        entityManager.flush();
        entityManager.clear();

        ClientJPA stored = clientRepository.findById(10).orElseThrow();
        assertThat(stored.getDni()).isEqualTo("11111111H");
        assertThat(stored.getPolicies()).hasSize(1);
        assertThat(policy.idPolicy()).isNotNull();
        assertThat(policyService.findByClient(10)).containsExactly(policy);
    }

    @Test
    void updateChangesClientData() {
        clientService.update(2, new ClientRequest(2, "87654321X", "Carlos", "Ruiz Gomez", LocalDate.of(1975, 11, 3)));
        entityManager.flush();
        entityManager.clear();

        assertThat(clientService.findById(2).surname()).isEqualTo("Ruiz Gomez");
    }

    @Test
    void updateRejectsDniOfAnotherClient() {
        ClientRequest request = new ClientRequest(2, "12345678Z", "Carlos", "Ruiz", null);

        assertThatThrownBy(() -> clientService.update(2, request)).isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void deleteClientRemovesItsPolicies() {
        clientService.delete(2);
        entityManager.flush();

        assertThat(clientRepository.existsById(2)).isFalse();
        assertThat(policyRepository.findAll()).extracting(p -> p.getNumPolicy()).doesNotContain("POL-0003");
        assertThat(clientService.findAll()).hasSize(1);
    }

    @Test
    void deletePolicyKeepsClient() {
        Integer idPolicy = policyService.findByClient(1).get(0).idPolicy();

        policyService.delete(idPolicy);
        entityManager.flush();
        entityManager.clear();

        assertThat(policyService.findByClient(1)).hasSize(1);
        assertThat(policyService.findAll()).hasSize(2);
        assertThatThrownBy(() -> policyService.findById(idPolicy)).isInstanceOf(NotFoundException.class);
    }
}
