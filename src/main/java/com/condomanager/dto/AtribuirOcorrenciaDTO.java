package com.condomanager.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Dados para atribuir uma ocorrência a um utilizador responsável.
 */
public record AtribuirOcorrenciaDTO(

        @NotNull(message = "O utilizador responsável é obrigatório")
        Long utilizadorId
) {
}
