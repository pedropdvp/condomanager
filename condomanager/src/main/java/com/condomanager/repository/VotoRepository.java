package com.condomanager.repository;

import com.condomanager.model.Voto;
import com.condomanager.model.enums.RespostaVoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    long countByVotacaoIdAndResposta(Long votacaoId, RespostaVoto resposta);
    boolean existsByVotacaoIdAndCondominoId(Long votacaoId, Long condominoId);
}
