package com.condomanager.dto;

import java.time.LocalDateTime;

/**
 * Representação pública (metadados) de um documento. O caminho físico do ficheiro
 * não é exposto; o download faz-se por {@code /api/v1/documentos/{id}/download}.
 */
public record DocumentoResponse(
        Long id,
        Long condominioId,
        String nome,
        String tipo,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
