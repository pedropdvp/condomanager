package com.condomanager.dto;

import com.condomanager.model.EstadoVotacao;
import com.condomanager.model.TipoMaioria;

import java.time.LocalDateTime;

/**
 * Representação pública de uma votação.
 */
public record VotacaoResponse(
        Long id,
        Long reuniaoId,
        String tema,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        TipoMaioria tipoMaioria,
        EstadoVotacao estado,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
