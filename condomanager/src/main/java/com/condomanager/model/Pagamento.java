package com.condomanager.model;

import com.condomanager.model.enums.MetodoPagamento;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento")
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MetodoPagamento metodo;

    @Column(length = 20)
    private String estado = "CONFIRMADO";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_quota")
    private Quota quota;

    public Pagamento() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public MetodoPagamento getMetodo() { return metodo; }
    public void setMetodo(MetodoPagamento metodo) { this.metodo = metodo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Quota getQuota() { return quota; }
    public void setQuota(Quota quota) { this.quota = quota; }
}
