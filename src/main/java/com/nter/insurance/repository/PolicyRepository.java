package com.nter.insurance.repository;

import com.nter.insurance.entity.PolicyJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<PolicyJPA, Integer> {

    List<PolicyJPA> findByClientIdClient(Integer idClient);

    boolean existsByNumPolicy(String numPolicy);
}
