package com.condomanager.model;

import com.condomanager.model.enums.EstadoVotacao;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votacao")
public class Votacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_votacao")
    private Long id;

    @Column(nullable = false, length = 255)
    private String tema;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoVotacao estado = EstadoVotacao.ABERTA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reuniao")
    private Reuniao reuniao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio")
    private Condominio condominio;

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

    public Reuniao getReuniao() { return reuniao; }
    public void setReuniao(Reuniao reuniao) { this.reuniao = reuniao; }

    public Condominio getCondominio() { return condominio; }
    public void setCondominio(Condominio condominio) { this.condominio = condominio; }
}
