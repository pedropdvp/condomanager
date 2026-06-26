package com.condomanager.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Linha de um relatório tabular genérico (até 5 colunas de texto).
 * Bean para o JasperReports — requer getters (gerados pelo Lombok).
 */
@Getter
@AllArgsConstructor
public class RelatorioGenericoRow {

    private final String c1;
    private final String c2;
    private final String c3;
    private final String c4;
    private final String c5;
}
