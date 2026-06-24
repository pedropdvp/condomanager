package com.condomanager.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Pedido de geração de quotas mensais para todas as frações de um condomínio.
 */
public record GerarQuotasDTO(

        @NotNull(message = "O condomínio é obrigatório")
        Long condominioId,

        @NotNull(message = "O mês é obrigatório")
        @Min(1) @Max(12)
        Integer mes,

        @NotNull(message = "O ano é obrigatório")
        @Min(2000) @Max(2100)
        Integer ano
) {
}
