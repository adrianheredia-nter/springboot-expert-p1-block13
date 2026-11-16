package com.nter.insurance.service;

import com.nter.insurance.dto.ClientRequest;
import com.nter.insurance.dto.ClientResponse;
import com.nter.insurance.entity.ClientJPA;
import com.nter.insurance.exception.AlreadyExistsException;
import com.nter.insurance.exception.InvalidDataException;
import com.nter.insurance.exception.NotFoundException;
import com.nter.insurance.mapper.ClientMapper;
import com.nter.insurance.mapper.PolicyMapper;
import com.nter.insurance.repository.ClientRepository;
import com.nter.insurance.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    private static final LocalDate BIRTHDATE = LocalDate.of(1990, 5, 20);

    @Mock
    private ClientRepository clientRepository;

    @Spy
    private ClientMapper clientMapper = new ClientMapper(new PolicyMapper());

    @InjectMocks
    private ClientServiceImpl clientService;

    @Captor
    private ArgumentCaptor<ClientJPA> clientCaptor;

    @Test
    void createSavesNormalizedClient() {
        ClientRequest request = new ClientRequest(3, " 11111111-h ", " Ana ", "Lopez", BIRTHDATE);
        when(clientRepository.existsById(3)).thenReturn(false);
        when(clientRepository.existsByDniIgnoreCase("11111111H")).thenReturn(false);
        when(clientRepository.save(any(ClientJPA.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientResponse response = clientService.create(request);

        verify(clientRepository).save(clientCaptor.capture());
        ClientJPA saved = clientCaptor.getValue();
        assertThat(saved.getIdClient()).isEqualTo(3);
        assertThat(saved.getDni()).isEqualTo("11111111H");
        assertThat(saved.getName()).isEqualTo("Ana");
        assertThat(saved.getPolicies()).isEmpty();
        assertThat(response.dni()).isEqualTo("11111111H");
        assertThat(response.birthdate()).isEqualTo(BIRTHDATE);
    }

    @Test
    void createReturnsMappedResponseFromRepository() {
        ClientRequest request = new ClientRequest(4, "22222222J", "Pedro", "Sanz", BIRTHDATE);
        ClientJPA stored = ClientJPA.builder()
                .idClient(4).dni("22222222J").name("Pedro").surname("Sanz").birthdate(BIRTHDATE).build();
        when(clientRepository.existsById(4)).thenReturn(false);
        when(clientRepository.existsByDniIgnoreCase("22222222J")).thenReturn(false);
        when(clientRepository.save(any(ClientJPA.class))).thenReturn(stored);

        ClientResponse response = clientService.create(request);

        assertThat(response).isEqualTo(new ClientResponse(4, "22222222J", "Pedro", "Sanz", BIRTHDATE, List.of()));
        verify(clientMapper).toResponse(stored);
    }

    @Test
    void createFailsWhenIdIsTaken() {
        when(clientRepository.existsById(1)).thenReturn(true);

        ClientRequest request = new ClientRequest(1, "12345678Z", "Laura", "Martin", BIRTHDATE);

        assertThatThrownBy(() -> clientService.create(request))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("Client 1 already exists");
        verify(clientRepository, never()).save(any());
    }

    @Test
    void createFailsWhenDniIsTaken() {
        when(clientRepository.existsById(5)).thenReturn(false);
        when(clientRepository.existsByDniIgnoreCase("12345678Z")).thenReturn(true);

        ClientRequest request = new ClientRequest(5, "12345678z", "Laura", "Martin", BIRTHDATE);

        assertThatThrownBy(() -> clientService.create(request))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("There is already a client with DNI 12345678Z");
    }

    @Test
    void createFailsWithWrongDniLetter() {
        ClientRequest request = new ClientRequest(5, "12345678A", "Laura", "Martin", BIRTHDATE);

        assertThatThrownBy(() -> clientService.create(request)).isInstanceOf(InvalidDataException.class);
        verify(clientRepository, never()).existsById(any());
    }

    @Test
    void findByIdFailsWhenClientDoesNotExist() {
        when(clientRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findById(99))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Client 99 not found");
    }
}
