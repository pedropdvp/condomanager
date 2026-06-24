package com.condomanager.dto;

import com.condomanager.model.TipoCondomino;

import java.time.LocalDateTime;

/**
 * Representação pública de um condómino.
 */
public record CondominoResponse(
        Long id,
        Long fracaoId,
        String nome,
        String nif,
        String email,
        String telefone,
        TipoCondomino tipo,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
