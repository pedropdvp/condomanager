package com.condomanager.dto;

import com.condomanager.model.RespostaVoto;
import jakarta.validation.constraints.NotNull;

/**
 * Voto do próprio condómino autenticado (o condómino é inferido da conta).
 */
public record VotoProprioDTO(

        @NotNull(message = "A resposta é obrigatória")
        RespostaVoto resposta
) {
}
