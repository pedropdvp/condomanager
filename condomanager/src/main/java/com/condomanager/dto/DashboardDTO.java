package com.condomanager.dto;

import java.math.BigDecimal;

/**
 * Indicadores em tempo real do dashboard (Modulo 16 / RF18).
 */
public class DashboardDTO {

    private long condominiosAtivos;
    private long condominosAtivos;
    private long fracoes;
    private long quotasPorPagar;
    private BigDecimal dividasPendentes;
    private BigDecimal receitaMesAtual;

    public long getCondominiosAtivos() { return condominiosAtivos; }
    public void setCondominiosAtivos(long v) { this.condominiosAtivos = v; }

    public long getCondominosAtivos() { return condominosAtivos; }
    public void setCondominosAtivos(long v) { this.condominosAtivos = v; }

    public long getFracoes() { return fracoes; }
    public void setFracoes(long v) { this.fracoes = v; }

    public long getQuotasPorPagar() { return quotasPorPagar; }
    public void setQuotasPorPagar(long v) { this.quotasPorPagar = v; }

    public BigDecimal getDividasPendentes() { return dividasPendentes; }
    public void setDividasPendentes(BigDecimal v) { this.dividasPendentes = v; }

    public BigDecimal getReceitaMesAtual() { return receitaMesAtual; }
    public void setReceitaMesAtual(BigDecimal v) { this.receitaMesAtual = v; }
}
