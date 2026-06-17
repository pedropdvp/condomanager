package com.condomanager.repository;

import com.condomanager.model.Votacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VotacaoRepository extends JpaRepository<Votacao, Long> {
    List<Votacao> findByCondominioId(Long condominioId);
}
