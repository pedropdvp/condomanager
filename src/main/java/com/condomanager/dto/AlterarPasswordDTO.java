package com.condomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados para definir uma nova password de um utilizador.
 */
public record AlterarPasswordDTO(

        @NotBlank(message = "A nova password é obrigatória")
        @Size(min = 6, max = 72, message = "A password deve ter entre 6 e 72 caracteres")
        String novaPassword
) {
}
