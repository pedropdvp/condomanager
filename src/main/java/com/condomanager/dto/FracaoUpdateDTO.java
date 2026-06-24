package com.condomanager.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Dados para atualizar uma fração (o condomínio e o edifício são imutáveis aqui).
 */
public record FracaoUpdateDTO(

        @NotBlank(message = "O número da fração é obrigatório")
        @Size(max = 20)
        String numero,

        @Size(max = 20)
        String tipologia,

        @NotNull(message = "A permilagem é obrigatória")
        @PositiveOrZero(message = "A permilagem não pode ser negativa")
        @DecimalMax(value = "1000.0000", message = "A permilagem é expressa em milésimos (máx. 1000)")
        @Digits(integer = 4, fraction = 4)
        BigDecimal permilagem,

        @PositiveOrZero(message = "A área não pode ser negativa")
        @Digits(integer = 6, fraction = 2)
        BigDecimal areaM2
) {
}
