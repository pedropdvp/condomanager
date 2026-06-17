package com.condomanager.dto;

import com.condomanager.model.enums.EstadoQuota;

import java.math.BigDecimal;

public class QuotaDTO {

    private Long id;
    private int mes;
    private int ano;
    private BigDecimal valor;
    private EstadoQuota estado;
    private Long fracaoId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public EstadoQuota getEstado() { return estado; }
    public void setEstado(EstadoQuota estado) { this.estado = estado; }

    public Long getFracaoId() { return fracaoId; }
    public void setFracaoId(Long fracaoId) { this.fracaoId = fracaoId; }
}
