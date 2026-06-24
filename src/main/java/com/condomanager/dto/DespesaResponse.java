package com.condomanager.dto;

import com.condomanager.model.CategoriaDespesa;
import com.condomanager.model.EstadoDespesa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representação pública de uma despesa.
 */
public record DespesaResponse(
        Long id,
        Long condominioId,
        String descricao,
        CategoriaDespesa categoria,
        BigDecimal valor,
        LocalDate dataDespesa,
        EstadoDespesa estado,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
