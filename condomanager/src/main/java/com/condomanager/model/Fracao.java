package com.condomanager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fracao")
public class Fracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fracao")
    private Long id;

    @Column(nullable = false, length = 20)
    private String numero;

    private Integer piso;

    /** Permilagem: a soma das fracoes de um condominio deve totalizar 1000. */
    @Column(precision = 7, scale = 3)
    private BigDecimal permilagem;

    @Column(length = 30)
    private String tipologia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_edificio")
    private Edificio edificio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio")
    private Condominio condominio;

    public Fracao() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }

    public BigDecimal getPermilagem() { return permilagem; }
    public void setPermilagem(BigDecimal permilagem) { this.permilagem = permilagem; }

    public String getTipologia() { return tipologia; }
    public void setTipologia(String tipologia) { this.tipologia = tipologia; }

    public Edificio getEdificio() { return edificio; }
    public void setEdificio(Edificio edificio) { this.edificio = edificio; }

    public Condominio getCondominio() { return condominio; }
    public void setCondominio(Condominio condominio) { this.condominio = condominio; }
}
