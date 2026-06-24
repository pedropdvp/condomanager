package com.condomanager.repository;

import com.condomanager.model.EmpresaGestao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaGestaoRepository extends JpaRepository<EmpresaGestao, Long> {

    boolean existsByNif(String nif);

    boolean existsByEmail(String email);
}
