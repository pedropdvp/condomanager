package com.condomanager.dto;

import com.condomanager.model.enums.EstadoOcorrencia;
import com.condomanager.model.enums.Prioridade;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class OcorrenciaDTO {

    private Long id;

    @NotBlank
    private String titulo;

    private String descricao;
    private EstadoOcorrencia estado;
    private Prioridade prioridade;
    private LocalDateTime dataRegisto;
    private Long condominoId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public EstadoOcorrencia getEstado() { return estado; }
    public void setEstado(EstadoOcorrencia estado) { this.estado = estado; }

    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }

    public LocalDateTime getDataRegisto() { return dataRegisto; }
    public void setDataRegisto(LocalDateTime dataRegisto) { this.dataRegisto = dataRegisto; }

    public Long getCondominoId() { return condominoId; }
    public void setCondominoId(Long condominoId) { this.condominoId = condominoId; }
}
