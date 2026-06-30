package com.condomanager.dto;

/**
 * Contexto do condómino autenticado (para o Portal do Condómino / self-service).
 */
public record ContextoCondominoResponse(
        Long condominoId,
        String condominoNome,
        Long condominioId,
        String condominioNome,
        Long fracaoId,
        String fracaoNumero) {
}
