package com.condomanager.dto;

import com.condomanager.model.EstadoEmpresa;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dados para atualizar uma empresa de gestão. O NIF é imutável e não é alterável aqui.
 */
public record EmpresaUpdateDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 150)
        String email,

        @Size(max = 30)
        String telefone,

        @Size(max = 255)
        String morada,

        @NotNull(message = "O estado é obrigatório")
        EstadoEmpresa estado
) {
}
