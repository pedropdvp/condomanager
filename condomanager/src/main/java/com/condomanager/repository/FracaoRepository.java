package com.condomanager.repository;

import com.condomanager.model.Fracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FracaoRepository extends JpaRepository<Fracao, Long> {
    List<Fracao> findByCondominioId(Long condominioId);
    long countByCondominioEmpresaId(Long empresaId);
}
