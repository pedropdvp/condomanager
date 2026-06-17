package com.condomanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ata")
public class Ata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ata")
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_reuniao")
    private LocalDate dataReuniao;

    @Column(length = 255)
    private String ficheiro;

    @Column(nullable = false)
    private boolean arquivada = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio")
    private Condominio condominio;

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

    public Condominio getCondominio() { return condominio; }
    public void setCondominio(Condominio condominio) { this.condominio = condominio; }
}
