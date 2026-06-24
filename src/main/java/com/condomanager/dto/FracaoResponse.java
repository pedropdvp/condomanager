package com.condomanager.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representação pública de uma fração.
 */
public record FracaoResponse(
        Long id,
        Long condominioId,
        Long edificioId,
        String numero,
        String tipologia,
        BigDecimal permilagem,
        BigDecimal areaM2,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
