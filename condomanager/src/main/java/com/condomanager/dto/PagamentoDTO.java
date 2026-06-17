package com.condomanager.dto;

import com.condomanager.model.enums.MetodoPagamento;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagamentoDTO {

    private Long id;

    @NotNull
    private BigDecimal valor;

    private LocalDate data;

    @NotNull
    private MetodoPagamento metodo;

    private String estado;

    @NotNull
    private Long quotaId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public MetodoPagamento getMetodo() { return metodo; }
    public void setMetodo(MetodoPagamento metodo) { this.metodo = metodo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Long getQuotaId() { return quotaId; }
    public void setQuotaId(Long quotaId) { this.quotaId = quotaId; }
}
