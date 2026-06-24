package com.condomanager.dto;

import com.condomanager.model.CategoriaDespesa;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dados para atualizar uma despesa (o condomínio é imutável aqui).
 */
public record DespesaUpdateDTO(

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 255)
        String descricao,

        @NotNull(message = "A categoria é obrigatória")
        CategoriaDespesa categoria,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser positivo")
        @Digits(integer = 8, fraction = 2)
        BigDecimal valor,

        @NotNull(message = "A data da despesa é obrigatória")
        LocalDate dataDespesa
) {
}
