package com.condomanager.model;

import com.condomanager.model.enums.EstadoOcorrencia;
import com.condomanager.model.enums.Prioridade;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ocorrencia")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ocorrencia")
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoOcorrencia estado = EstadoOcorrencia.ABERTA;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Prioridade prioridade = Prioridade.MEDIA;

    @Column(name = "data_registo")
    private LocalDateTime dataRegisto = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condomino")
    private Condomino condomino;

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

    public Condomino getCondomino() { return condomino; }
    public void setCondomino(Condomino condomino) { this.condomino = condomino; }
}
