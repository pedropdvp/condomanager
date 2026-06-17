package com.condomanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "edificio")
public class Edificio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_edificio")
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 255)
    private String morada;

    @Column(name = "num_pisos")
    private Integer numPisos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio")
    private Condominio condominio;

    public Edificio() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }

    public Integer getNumPisos() { return numPisos; }
    public void setNumPisos(Integer numPisos) { this.numPisos = numPisos; }

    public Condominio getCondominio() { return condominio; }
    public void setCondominio(Condominio condominio) { this.condominio = condominio; }
}
