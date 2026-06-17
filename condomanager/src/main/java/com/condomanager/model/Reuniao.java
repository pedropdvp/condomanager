package com.condomanager.model;

import com.condomanager.model.enums.EstadoReuniao;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reuniao")
public class Reuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reuniao")
    private Long id;

    @Column(length = 150)
    private String assunto;

    @Column(nullable = false)
    private LocalDate data;

    private LocalTime hora;

    @Column(length = 150)
    private String local;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoReuniao estado = EstadoReuniao.AGENDADA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio")
    private Condominio condominio;

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

    public Condominio getCondominio() { return condominio; }
    public void setCondominio(Condominio condominio) { this.condominio = condominio; }
}
