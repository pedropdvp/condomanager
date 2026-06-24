package com.condomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Dados para atualizar um utilizador (nome, estado e perfis). O email é imutável aqui.
 */
public record UtilizadorUpdateDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String nome,

        @NotNull(message = "O estado (ativo) é obrigatório")
        Boolean ativo,

        @NotEmpty(message = "Pelo menos um perfil é obrigatório")
        Set<String> perfis
) {
}
