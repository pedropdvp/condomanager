package com.condomanager.dto;

import com.condomanager.model.MetodoPagamento;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Dados para registar um pagamento de uma quota.
 */
public record PagamentoCreateDTO(

        @NotNull(message = "A quota é obrigatória")
        Long quotaId,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser positivo")
        @Digits(integer = 8, fraction = 2)
        BigDecimal valor,

        @NotNull(message = "O método é obrigatório")
        MetodoPagamento metodo,

        /** Opcional; se omitido, assume-se o momento atual. */
        LocalDateTime dataPagamento
) {
}
