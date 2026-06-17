package com.condomanager.dto;

import com.condomanager.model.enums.EstadoReuniao;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReuniaoDTO {

    private Long id;
    private String assunto;

    @NotNull
    private LocalDate data;

    private LocalTime hora;
    private String local;
    private EstadoReuniao estado;

    @NotNull
    private Long condominioId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public EstadoReuniao getEstado() { return estado; }
    public void setEstado(EstadoReuniao estado) { this.estado = estado; }

    public Long getCondominioId() { return condominioId; }
    public void setCondominioId(Long condominioId) { this.condominioId = condominioId; }
}
