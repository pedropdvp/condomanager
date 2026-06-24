package com.condomanager.repository;

import com.condomanager.model.Utilizador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilizadorRepository extends JpaRepository<Utilizador, Long> {

    Optional<Utilizador> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Utilizador> findByIdEmpresa(Long idEmpresa, Pageable pageable);

    long countByIdEmpresa(Long idEmpresa);
}
