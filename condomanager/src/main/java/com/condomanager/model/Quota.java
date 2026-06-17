package com.condomanager.model;

import com.condomanager.model.enums.EstadoQuota;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "quota")
public class Quota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quota")
    private Long id;

    @Column(nullable = false)
    private int mes;

    @Column(nullable = false)
    private int ano;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoQuota estado = EstadoQuota.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fracao")
    private Fracao fracao;

    public Quota() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public EstadoQuota getEstado() { return estado; }
    public void setEstado(EstadoQuota estado) { this.estado = estado; }

    public Fracao getFracao() { return fracao; }
    public void setFracao(Fracao fracao) { this.fracao = fracao; }
}
