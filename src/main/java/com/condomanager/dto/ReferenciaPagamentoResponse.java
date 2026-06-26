package com.condomanager.dto;

import java.math.BigDecimal;

/**
 * Referência de pagamento online (estilo Multibanco) gerada para uma quota.
 *
 * <p><strong>Scaffolding</strong>: hoje é simulada (sem gateway real). O campo
 * {@code nota} deixa isso explícito; basta ligar Stripe/SIBS para produção.</p>
 */
public record ReferenciaPagamentoResponse(
        String entidade,
        String referencia,
        BigDecimal valor,
        String instrucoes,
        String nota) {
}
