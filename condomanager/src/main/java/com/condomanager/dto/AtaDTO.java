package com.condomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AtaDTO {

    private Long id;

    @NotBlank
    private String titulo;

    private String descricao;
    private LocalDate dataReuniao;
    private String ficheiro;
    private boolean arquivada;

    @NotNull
    private Long condominioId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataReuniao() { return dataReuniao; }
    public void setDataReuniao(LocalDate dataReuniao) { this.dataReuniao = dataReuniao; }

    public String getFicheiro() { return ficheiro; }
    public void setFicheiro(String ficheiro) { this.ficheiro = ficheiro; }

    public boolean isArquivada() { return arquivada; }
    public void setArquivada(boolean arquivada) { this.arquivada = arquivada; }

    public Long getCondominioId() { return condominioId; }
    public void setCondominioId(Long condominioId) { this.condominioId = condominioId; }
}
