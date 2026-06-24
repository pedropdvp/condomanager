package com.condomanager.dto;

import com.condomanager.model.EstadoVotacao;
import com.condomanager.model.TipoMaioria;

import java.math.BigDecimal;

/**
 * Resultado da contagem de uma votação (pesos em permilagem).
 *
 * @param capitalTotal    soma da permilagem de todas as frações do condomínio
 * @param capitalPresente soma da permilagem que votou (SIM + NAO + ABSTENCAO)
 * @param somaSim         permilagem a favor
 * @param somaNao         permilagem contra
 * @param somaAbstencao   permilagem em abstenção
 * @param numeroVotos     número de votos expressos
 * @param aprovado        se a deliberação foi aprovada segundo a maioria exigida
 */
public record ResultadoVotacaoResponse(
        Long votacaoId,
        String tema,
        TipoMaioria tipoMaioria,
        EstadoVotacao estado,
        BigDecimal capitalTotal,
        BigDecimal capitalPresente,
        BigDecimal somaSim,
        BigDecimal somaNao,
        BigDecimal somaAbstencao,
        int numeroVotos,
        boolean aprovado
) {
}
