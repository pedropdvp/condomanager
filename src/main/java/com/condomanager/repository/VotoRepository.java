package com.condomanager.repository;

import com.condomanager.model.Voto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    Optional<Voto> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    boolean existsByVotacao_IdAndCondomino_Id(Long votacaoId, Long condominoId);

    Page<Voto> findByVotacao_Id(Long votacaoId, Pageable pageable);

    /** Todos os votos de uma votação (usado na contagem). */
    List<Voto> findByVotacao_Id(Long votacaoId);
}
