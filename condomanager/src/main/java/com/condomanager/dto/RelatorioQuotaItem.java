package com.condomanager.dto;

import java.math.BigDecimal;

/**
 * Linha do relatorio de quotas em atraso/pendentes (fonte de dados do JasperReports).
 * Os nomes dos getters correspondem aos campos definidos no template .jrxml.
 */
public class RelatorioQuotaItem {

    private String condominio;
    private String fracao;
    private String periodo;
    private BigDecimal valor;
    private String estado;

    public RelatorioQuotaItem(String condominio, String fracao, String periodo,
                              BigDecimal valor, String estado) {
        this.condominio = condominio;
        this.fracao = fracao;
        this.periodo = periodo;
        this.valor = valor;
        this.estado = estado;
    }

    public String getCondominio() { return condominio; }
    public String getFracao() { return fracao; }
    public String getPeriodo() { return periodo; }
    public BigDecimal getValor() { return valor; }
    public String getEstado() { return estado; }
}
