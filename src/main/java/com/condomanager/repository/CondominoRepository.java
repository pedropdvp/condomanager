package com.condomanager.repository;

import com.condomanager.model.Condomino;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CondominoRepository extends JpaRepository<Condomino, Long> {

    Optional<Condomino> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    Page<Condomino> findByFracao_Id(Long fracaoId, Pageable pageable);

    /** Todos os condóminos de um condomínio (usado nas convocatórias). */
    java.util.List<Condomino> findByFracao_Condominio_Id(Long condominioId);
}
