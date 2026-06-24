package com.condomanager.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representação pública de uma ata. {@code temFicheiro} indica se há ficheiro anexo
 * descarregável em {@code /api/v1/atas/{id}/download}.
 */
public record AtaResponse(
        Long id,
        Long reuniaoId,
        String titulo,
        String descricao,
        LocalDate dataReuniao,
        boolean temFicheiro,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
