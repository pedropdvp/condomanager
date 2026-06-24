package com.condomanager.dto;

import com.condomanager.model.EstadoPagamento;
import com.condomanager.model.MetodoPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representação pública de um pagamento.
 */
public record PagamentoResponse(
        Long id,
        Long quotaId,
        BigDecimal valor,
        LocalDateTime dataPagamento,
        MetodoPagamento metodo,
        EstadoPagamento estado,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
