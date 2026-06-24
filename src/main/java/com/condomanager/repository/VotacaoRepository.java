package com.condomanager.repository;

import com.condomanager.model.EstadoVotacao;
import com.condomanager.model.Votacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VotacaoRepository extends JpaRepository<Votacao, Long> {

    Optional<Votacao> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    Page<Votacao> findByReuniao_Id(Long reuniaoId, Pageable pageable);

    Page<Votacao> findByEstado(EstadoVotacao estado, Pageable pageable);
}
