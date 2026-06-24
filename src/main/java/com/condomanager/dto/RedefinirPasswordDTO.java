package com.condomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Reposição de password a partir de um token de recuperação.
 */
public record RedefinirPasswordDTO(

        @NotBlank(message = "O token é obrigatório")
        String token,

        @NotBlank(message = "A nova password é obrigatória")
        @Size(min = 6, max = 72, message = "A password deve ter entre 6 e 72 caracteres")
        String novaPassword
) {
}
