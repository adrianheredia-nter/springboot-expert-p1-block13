package com.nter.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nter.insurance.dto.ClientRequest;
import com.nter.insurance.dto.ClientResponse;
import com.nter.insurance.dto.PolicyResponse;
import com.nter.insurance.exception.AlreadyExistsException;
import com.nter.insurance.exception.NotFoundException;
import com.nter.insurance.service.ClientService;
import com.nter.insurance.service.PolicyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    private static final LocalDate BIRTHDATE = LocalDate.of(1990, 5, 20);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @MockBean
    private PolicyService policyService;

    @Test
    void findByIdReturnsClientWithPolicies() throws Exception {
        PolicyResponse policy = new PolicyResponse(1, "POL-0001", "health", "dental", 1);
        when(clientService.findById(1))
                .thenReturn(new ClientResponse(1, "12345678Z", "Laura", "Martin", BIRTHDATE, List.of(policy)));

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idClient").value(1))
                .andExpect(jsonPath("$.dni").value("12345678Z"))
                .andExpect(jsonPath("$.birthdate").value("1990-05-20"))
                .andExpect(jsonPath("$.policies", hasSize(1)))
                .andExpect(jsonPath("$.policies[0].numPolicy").value("POL-0001"));
    }

    @Test
    void createReturnsCreatedClient() throws Exception {
        ClientRequest request = new ClientRequest(3, "11111111H", "Ana", "Lopez", BIRTHDATE);
        when(clientService.create(any(ClientRequest.class)))
                .thenReturn(new ClientResponse(3, "11111111H", "Ana", "Lopez", BIRTHDATE, List.of()));

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idClient").value(3))
                .andExpect(jsonPath("$.name").value("Ana"))
                .andExpect(jsonPath("$.policies", hasSize(0)));

        verify(clientService).create(request);
    }

    @Test
    void findByIdReturnsNotFoundWhenClientDoesNotExist() throws Exception {
        when(clientService.findById(99)).thenThrow(new NotFoundException("Client 99 not found"));

        mockMvc.perform(get("/api/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Client 99 not found"));
    }

    @Test
    void createReturnsConflictWhenClientAlreadyExists() throws Exception {
        ClientRequest request = new ClientRequest(1, "12345678Z", "Laura", "Martin", BIRTHDATE);
        when(clientService.create(any(ClientRequest.class))).thenThrow(new AlreadyExistsException("Client 1 already exists"));

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Client 1 already exists"));
    }

    @Test
    void createRejectsInvalidBody() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idClient\": 4, \"dni\": \"\", \"name\": \"Ana\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("dni must not be blank, surname must not be blank"));

        verifyNoInteractions(clientService);
    }

    @Test
    void createRejectsMalformedJson() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idClient\": "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed request body"));
    }

    @Test
    void updateAndDeleteDelegateToService() throws Exception {
        ClientRequest request = new ClientRequest(1, "12345678Z", "Laura", "Garcia", BIRTHDATE);
        when(clientService.update(eq(1), any(ClientRequest.class)))
                .thenReturn(new ClientResponse(1, "12345678Z", "Laura", "Garcia", BIRTHDATE, List.of()));

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surname").value("Garcia"));

        mockMvc.perform(delete("/api/clients/1")).andExpect(status().isNoContent());
        verify(clientService).delete(1);
    }

    @Test
    void deleteReturnsNotFoundWhenClientDoesNotExist() throws Exception {
        doThrow(new NotFoundException("Client 99 not found")).when(clientService).delete(99);

        mockMvc.perform(delete("/api/clients/99")).andExpect(status().isNotFound());
    }
}
