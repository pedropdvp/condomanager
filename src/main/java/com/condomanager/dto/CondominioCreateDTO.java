package com.condomanager.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Dados para criar um condomínio. O {@code id_empresa} é atribuído automaticamente
 * a partir do tenant do utilizador autenticado.
 */
public record CondominioCreateDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String nome,

        @Size(max = 255)
        String morada,

        @NotNull(message = "O orçamento anual é obrigatório")
        @PositiveOrZero(message = "O orçamento anual não pode ser negativo")
        @Digits(integer = 10, fraction = 2)
        BigDecimal orcamentoAnual
) {
}
