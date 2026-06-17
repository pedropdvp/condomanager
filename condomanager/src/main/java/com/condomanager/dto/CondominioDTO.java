package com.condomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CondominioDTO {

    private Long id;

    @NotBlank
    private String nome;

    private String morada;

    private BigDecimal orcamentoAnual;

    @NotNull
    private Long empresaId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }

    public BigDecimal getOrcamentoAnual() { return orcamentoAnual; }
    public void setOrcamentoAnual(BigDecimal orcamentoAnual) { this.orcamentoAnual = orcamentoAnual; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
}
