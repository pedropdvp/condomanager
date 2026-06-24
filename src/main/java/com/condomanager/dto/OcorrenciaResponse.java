package com.condomanager.dto;

import com.condomanager.model.EstadoOcorrencia;
import com.condomanager.model.PrioridadeOcorrencia;

import java.time.LocalDateTime;

/**
 * Representação pública de uma ocorrência.
 */
public record OcorrenciaResponse(
        Long id,
        Long condominioId,
        Long condominoId,
        Long responsavelId,
        String titulo,
        String descricao,
        EstadoOcorrencia estado,
        PrioridadeOcorrencia prioridade,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
