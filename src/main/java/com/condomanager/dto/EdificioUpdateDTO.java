package com.condomanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados para atualizar um edifício (o condomínio associado é imutável aqui).
 */
public record EdificioUpdateDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100)
        String nome,

        @Size(max = 50)
        String bloco,

        @Min(value = 0, message = "O número de pisos não pode ser negativo")
        Integer numeroPisos
) {
}
