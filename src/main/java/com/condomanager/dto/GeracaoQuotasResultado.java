package com.condomanager.dto;

import java.math.BigDecimal;

/**
 * Resumo do resultado da geração de quotas.
 *
 * @param mes              mês gerado
 * @param ano              ano gerado
 * @param quotasGeradas    número de quotas criadas
 * @param fracoesIgnoradas frações que já tinham quota para o período (saltadas)
 * @param valorTotal       soma do valor das quotas geradas
 */
public record GeracaoQuotasResultado(
        int mes,
        int ano,
        int quotasGeradas,
        int fracoesIgnoradas,
        BigDecimal valorTotal
) {
}
