package com.condomanager.repository;

import com.condomanager.model.Condominio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CondominioRepository extends JpaRepository<Condominio, Long> {
    List<Condominio> findByEmpresaId(Long empresaId);
}
