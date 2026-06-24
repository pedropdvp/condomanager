package com.condomanager.dto;

import com.condomanager.model.RespostaVoto;

import java.time.LocalDateTime;

/**
 * Representação pública de um voto.
 */
public record VotoResponse(
        Long id,
        Long votacaoId,
        Long condominoId,
        RespostaVoto resposta,
        Long idEmpresa,
        LocalDateTime createdAt
) {
}
