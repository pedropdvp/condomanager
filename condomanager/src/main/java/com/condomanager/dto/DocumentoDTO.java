package com.condomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class DocumentoDTO {

    private Long id;

    @NotBlank
    private String nome;

    private String tipo;
    private String ficheiro;
    private LocalDateTime dataUpload;

    @NotNull
    private Long condominioId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFicheiro() { return ficheiro; }
    public void setFicheiro(String ficheiro) { this.ficheiro = ficheiro; }

    public LocalDateTime getDataUpload() { return dataUpload; }
    public void setDataUpload(LocalDateTime dataUpload) { this.dataUpload = dataUpload; }

    public Long getCondominioId() { return condominioId; }
    public void setCondominioId(Long condominioId) { this.condominioId = condominioId; }
}
