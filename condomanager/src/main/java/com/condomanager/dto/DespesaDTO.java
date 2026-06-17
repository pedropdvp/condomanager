package com.condomanager.dto;

import com.condomanager.model.enums.EstadoDespesa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DespesaDTO {

    private Long id;

    @NotBlank
    private String descricao;

    @NotNull
    private BigDecimal valor;

    private LocalDate data;
    private String categoria;
    private EstadoDespesa estado;

    @NotNull
    private Long condominioId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public EstadoDespesa getEstado() { return estado; }
    public void setEstado(EstadoDespesa estado) { this.estado = estado; }

    public Long getCondominioId() { return condominioId; }
    public void setCondominioId(Long condominioId) { this.condominioId = condominioId; }
}
