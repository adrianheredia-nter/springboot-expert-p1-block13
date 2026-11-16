package com.nter.insurance.repository;

import com.nter.insurance.entity.ClientJPA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientJPA, Integer> {

    boolean existsByDniIgnoreCase(String dni);
}
