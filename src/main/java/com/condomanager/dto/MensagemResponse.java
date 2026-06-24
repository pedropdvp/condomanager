package com.condomanager.dto;

import com.condomanager.model.TipoMensagem;

import java.time.LocalDateTime;

/**
 * Representação pública de uma mensagem.
 */
public record MensagemResponse(
        Long id,
        TipoMensagem tipo,
        String assunto,
        String conteudo,
        LocalDateTime dataEnvio,
        Long origemId,
        String origemNome,
        Long destinoId,
        boolean lida,
        Long idEmpresa,
        LocalDateTime createdAt
) {
}
