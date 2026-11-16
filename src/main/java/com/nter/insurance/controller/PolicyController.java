package com.nter.insurance.controller;

import com.nter.insurance.constants.AppConstants;
import com.nter.insurance.dto.PolicyRequest;
import com.nter.insurance.dto.PolicyResponse;
import com.nter.insurance.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppConstants.POLICIES_PATH)
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    public List<PolicyResponse> findAll() {
        return policyService.findAll();
    }

    @GetMapping("/{idPolicy}")
    public PolicyResponse findById(@PathVariable Integer idPolicy) {
        return policyService.findById(idPolicy);
    }

    @PostMapping
    public ResponseEntity<PolicyResponse> create(@Valid @RequestBody PolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.create(request));
    }

    @DeleteMapping("/{idPolicy}")
    public ResponseEntity<Void> delete(@PathVariable Integer idPolicy) {
        policyService.delete(idPolicy);
        return ResponseEntity.noContent().build();
    }
}
