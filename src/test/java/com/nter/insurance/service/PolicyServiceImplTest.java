package com.nter.insurance.service;

import com.nter.insurance.dto.PolicyRequest;
import com.nter.insurance.dto.PolicyResponse;
import com.nter.insurance.entity.ClientJPA;
import com.nter.insurance.entity.PolicyJPA;
import com.nter.insurance.exception.AlreadyExistsException;
import com.nter.insurance.exception.NotFoundException;
import com.nter.insurance.mapper.PolicyMapper;
import com.nter.insurance.repository.ClientRepository;
import com.nter.insurance.repository.PolicyRepository;
import com.nter.insurance.service.impl.PolicyServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private ClientRepository clientRepository;

    @Spy
    private PolicyMapper policyMapper;

    @InjectMocks
    private PolicyServiceImpl policyService;

    @Test
    void createLinksPolicyToClient() {
        ClientJPA client = ClientJPA.builder().idClient(1).dni("12345678Z").name("Laura").surname("Martin").build();
        when(policyRepository.existsByNumPolicy("POL-0100")).thenReturn(false);
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        when(policyRepository.save(any(PolicyJPA.class))).thenAnswer(invocation -> {
            PolicyJPA policy = invocation.getArgument(0);
            policy.setIdPolicy(10);
            return policy;
        });

        PolicyResponse response = policyService.create(new PolicyRequest(" POL-0100 ", "car", "full", 1));

        ArgumentCaptor<PolicyJPA> captor = ArgumentCaptor.forClass(PolicyJPA.class);
        verify(policyRepository).save(captor.capture());
        assertThat(captor.getValue().getNumPolicy()).isEqualTo("POL-0100");
        assertThat(captor.getValue().getClient()).isSameAs(client);
        assertThat(client.getPolicies()).containsExactly(captor.getValue());
        assertThat(response).isEqualTo(new PolicyResponse(10, "POL-0100", "car", "full", 1));
    }

    @Test
    void createFailsWhenClientDoesNotExist() {
        when(policyRepository.existsByNumPolicy("POL-0101")).thenReturn(false);
        when(clientRepository.findById(42)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.create(new PolicyRequest("POL-0101", "car", null, 42)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Client 42 not found");
        verify(policyRepository, never()).save(any());
    }

    @Test
    void createFailsWhenNumberIsTaken() {
        when(policyRepository.existsByNumPolicy("POL-0001")).thenReturn(true);

        assertThatThrownBy(() -> policyService.create(new PolicyRequest("POL-0001", "health", null, 1)))
                .isInstanceOf(AlreadyExistsException.class);
        verify(clientRepository, never()).findById(any());
    }
}
