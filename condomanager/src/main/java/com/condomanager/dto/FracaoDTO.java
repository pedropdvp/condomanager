package com.condomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class FracaoDTO {

    private Long id;

    @NotBlank
    private String numero;

    private Integer piso;
    private BigDecimal permilagem;
    private String tipologia;

    private Long edificioId;

    @NotNull
    private Long condominioId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }

    public BigDecimal getPermilagem() { return permilagem; }
    public void setPermilagem(BigDecimal permilagem) { this.permilagem = permilagem; }

    public String getTipologia() { return tipologia; }
    public void setTipologia(String tipologia) { this.tipologia = tipologia; }

    public Long getEdificioId() { return edificioId; }
    public void setEdificioId(Long edificioId) { this.edificioId = edificioId; }

    public Long getCondominioId() { return condominioId; }
    public void setCondominioId(Long condominioId) { this.condominioId = condominioId; }
}
