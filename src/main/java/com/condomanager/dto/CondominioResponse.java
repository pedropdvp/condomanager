package com.condomanager.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representação pública de um condomínio.
 */
public record CondominioResponse(
        Long id,
        String nome,
        String morada,
        BigDecimal orcamentoAnual,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
