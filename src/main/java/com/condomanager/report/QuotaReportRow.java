package com.condomanager.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Linha do relatório de quotas (bean para o JasperReports — requer getters).
 */
@Getter
@AllArgsConstructor
public class QuotaReportRow {

    private final String fracao;
    private final String periodo;
    private final BigDecimal valor;
    private final String estado;
}
