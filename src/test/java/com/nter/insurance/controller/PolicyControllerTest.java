package com.nter.insurance.controller;

import com.nter.insurance.dto.PolicyResponse;
import com.nter.insurance.exception.InvalidDataException;
import org.springframework.dao.DataIntegrityViolationException;
import com.nter.insurance.service.PolicyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PolicyController.class)
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyService policyService;

    @Test
    void findAllReturnsPolicies() throws Exception {
        when(policyService.findAll()).thenReturn(List.of(
                new PolicyResponse(1, "POL-0001", "health", "dental", 1),
                new PolicyResponse(2, "POL-0002", "home", "contents", 1)));

        mockMvc.perform(get("/api/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].branch").value("home"));
    }

    @Test
    void createReturnsCreatedPolicy() throws Exception {
        when(policyService.create(any())).thenReturn(new PolicyResponse(5, "POL-0005", "life", "term", 2));

        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numPolicy": "POL-0005", "branch": "life", "subBranch": "term", "idClient": 2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPolicy").value(5))
                .andExpect(jsonPath("$.idClient").value(2));
    }

    @Test
    void findByIdReturnsBadRequestOnInvalidData() throws Exception {
        when(policyService.findById(1)).thenThrow(new InvalidDataException("Invalid policy"));

        mockMvc.perform(get("/api/policies/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void createReturnsConflictWhenDatabaseRejectsDuplicate() throws Exception {
        when(policyService.create(any())).thenThrow(new DataIntegrityViolationException("unique constraint"));

        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numPolicy": "POL-0001", "branch": "life", "idClient": 2}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The data conflicts with an existing record"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/policies/3")).andExpect(status().isNoContent());

        verify(policyService).delete(3);
    }
}
