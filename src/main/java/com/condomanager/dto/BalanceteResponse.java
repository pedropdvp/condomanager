package com.condomanager.dto;

import java.math.BigDecimal;

/**
 * Balancete / resumo de tesouraria de um condomínio (valores em euros).
 *
 * <p>O <strong>fundo de reserva</strong> corresponde a ≥10% das quotas (obrigatório pela
 * Lei 8/2022, ver {@code docs/LEGAL_RULES.md} §3).</p>
 */
public record BalanceteResponse(
        Long condominioId,
        String condominioNome,
        BigDecimal orcamentoAnual,
        BigDecimal totalQuotas,
        BigDecimal quotasPagas,
        BigDecimal quotasPorCobrar,
        BigDecimal despesasAprovadas,
        BigDecimal despesasPendentes,
        BigDecimal saldo,
        BigDecimal fundoReserva) {
}
