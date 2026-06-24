package com.condomanager.dto;

import com.condomanager.model.RespostaVoto;
import jakarta.validation.constraints.NotNull;

/**
 * Dados para registar um voto numa votação (o {@code votacaoId} vem do caminho).
 */
public record VotoCreateDTO(

        @NotNull(message = "O condómino é obrigatório")
        Long condominoId,

        @NotNull(message = "A resposta é obrigatória")
        RespostaVoto resposta
) {
}
