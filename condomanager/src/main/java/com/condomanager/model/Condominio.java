package com.condomanager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "condominio")
public class Condominio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_condominio")
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 255)
    private String morada;

    @Column(name = "orcamento_anual", precision = 12, scale = 2)
    private BigDecimal orcamentoAnual = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empresa")
    private EmpresaGestao empresa;

    public Condominio() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }

    public BigDecimal getOrcamentoAnual() { return orcamentoAnual; }
    public void setOrcamentoAnual(BigDecimal orcamentoAnual) { this.orcamentoAnual = orcamentoAnual; }

    public EmpresaGestao getEmpresa() { return empresa; }
    public void setEmpresa(EmpresaGestao empresa) { this.empresa = empresa; }
}
