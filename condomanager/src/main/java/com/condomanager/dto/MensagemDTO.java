package com.condomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MensagemDTO {

    private Long id;

    @NotBlank
    private String assunto;

    private String conteudo;
    private LocalDateTime dataEnvio;

    /** TODOS, GRUPO ou CONDOMINO. */
    private String destino;

    private Long condominoId;

    @NotNull
    private Long condominioId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public Long getCondominoId() { return condominoId; }
    public void setCondominoId(Long condominoId) { this.condominoId = condominoId; }

    public Long getCondominioId() { return condominioId; }
    public void setCondominioId(Long condominioId) { this.condominioId = condominioId; }
}
