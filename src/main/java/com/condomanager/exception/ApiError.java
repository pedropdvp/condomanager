package com.condomanager.exception;

import java.time.Instant;
import java.util.List;

/**
 * Estrutura uniforme de resposta de erro da API (JSON).
 *
 * @param timestamp momento em que o erro ocorreu
 * @param status    código HTTP
 * @param error     descrição curta do estado HTTP
 * @param message   mensagem orientada ao consumidor da API
 * @param path      caminho do pedido que originou o erro
 * @param details   detalhes adicionais (ex.: erros de validação por campo)
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details
) {
}
