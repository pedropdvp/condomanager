package com.condomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EdificioDTO {

    private Long id;

    @NotBlank
    private String nome;

    private String morada;
    private Integer numPisos;

    @NotNull
    private Long condominioId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }

    public Integer getNumPisos() { return numPisos; }
    public void setNumPisos(Integer numPisos) { this.numPisos = numPisos; }

    public Long getCondominioId() { return condominioId; }
    public void setCondominioId(Long condominioId) { this.condominioId = condominioId; }
}
