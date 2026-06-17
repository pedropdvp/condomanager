package com.condomanager.dto;

import com.condomanager.model.enums.EstadoVotacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class VotacaoDTO {

    private Long id;

    @NotBlank
    private String tema;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private EstadoVotacao estado;
    private Long reuniaoId;

    @NotNull
    private Long condominioId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public EstadoVotacao getEstado() { return estado; }
    public void setEstado(EstadoVotacao estado) { this.estado = estado; }

    public Long getReuniaoId() { return reuniaoId; }
    public void setReuniaoId(Long reuniaoId) { this.reuniaoId = reuniaoId; }

    public Long getCondominioId() { return condominioId; }
    public void setCondominioId(Long condominioId) { this.condominioId = condominioId; }
}
