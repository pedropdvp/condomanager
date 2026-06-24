package com.condomanager.dto;

import com.condomanager.model.EstadoQuota;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representação pública de uma quota.
 */
public record QuotaResponse(
        Long id,
        Long fracaoId,
        int mes,
        int ano,
        BigDecimal valor,
        EstadoQuota estado,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
