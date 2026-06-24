package com.condomanager.dto;

import com.condomanager.model.TipoCondomino;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dados para criar um condómino, associado a uma fração do tenant.
 */
public record CondominoCreateDTO(

        @NotNull(message = "A fração é obrigatória")
        Long fracaoId,

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String nome,

        @Size(max = 20)
        String nif,

        @Email(message = "Email inválido")
        @Size(max = 150)
        String email,

        @Size(max = 30)
        String telefone,

        @NotNull(message = "O tipo é obrigatório")
        TipoCondomino tipo
) {
}
