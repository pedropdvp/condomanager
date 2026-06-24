package com.condomanager.dto;

import java.time.LocalDateTime;

/**
 * Representação pública de um edifício.
 */
public record EdificioResponse(
        Long id,
        Long condominioId,
        String nome,
        String bloco,
        Integer numeroPisos,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
