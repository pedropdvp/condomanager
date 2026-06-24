package com.condomanager.dto;

import com.condomanager.model.TipoMensagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Dados para enviar uma mensagem. Para {@code INDIVIDUAL}, {@code destinoId} é
 * obrigatório; para {@code GRUPO}, {@code destinatarios} é obrigatório; para
 * {@code BROADCAST}, ambos são ignorados.
 */
public record MensagemCreateDTO(

        @NotNull(message = "O tipo é obrigatório")
        TipoMensagem tipo,

        Long destinoId,

        List<Long> destinatarios,

        @NotBlank(message = "O assunto é obrigatório")
        @Size(max = 200)
        String assunto,

        @NotBlank(message = "O conteúdo é obrigatório")
        String conteudo
) {
}
