package com.condomanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados para criar uma empresa de gestão.
 */
public record EmpresaCreateDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String nome,

        @NotBlank(message = "O NIF é obrigatório")
        @Size(max = 20)
        String nif,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 150)
        String email,

        @Size(max = 30)
        String telefone,

        @Size(max = 255)
        String morada
) {
}
