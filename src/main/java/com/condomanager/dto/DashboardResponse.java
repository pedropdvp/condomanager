package com.condomanager.dto;

import com.condomanager.model.EstadoOcorrencia;
import com.condomanager.model.EstadoQuota;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Indicadores agregados da empresa (tenant) para o dashboard.
 */
public record DashboardResponse(
        Estrutura estrutura,
        Financeiro financeiro,
        Map<EstadoOcorrencia, Long> ocorrenciasPorEstado,
        long reunioesAgendadas
) {

    /** Contagens da estrutura física e de utilizadores. */
    public record Estrutura(
            long condominios,
            long edificios,
            long fracoes,
            long condominos,
            long utilizadores
    ) {
    }

    /** Indicadores financeiros: quotas por estado (contagem e valor) e total de despesas. */
    public record Financeiro(
            Map<EstadoQuota, Long> quotasContagem,
            Map<EstadoQuota, BigDecimal> quotasValor,
            BigDecimal totalDespesas
    ) {
    }
}
