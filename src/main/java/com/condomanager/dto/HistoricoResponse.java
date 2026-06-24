package com.condomanager.dto;

import java.time.LocalDateTime;

/**
 * Representação pública de um registo de auditoria.
 */
public record HistoricoResponse(
        Long id,
        Long idEmpresa,
        String utilizador,
        String operacao,
        LocalDateTime dataHora
) {
}
