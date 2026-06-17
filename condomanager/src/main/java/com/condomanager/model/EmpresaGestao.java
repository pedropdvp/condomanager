package com.condomanager.model;

import com.condomanager.model.enums.Plano;
import jakarta.persistence.*;

/**
 * Entidade raiz do modelo multi-tenant (SaaS).
 * Cada EmpresaGestao isola os seus proprios dados.
 */
@Entity
@Table(name = "empresa_gestao")
public class EmpresaGestao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 20, unique = true)
    private String nif;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(length = 255)
    private String morada;

    @Column(length = 20)
    private String estado = "ATIVA";

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Plano plano = Plano.STARTER;

    public EmpresaGestao() {
    }

    public EmpresaGestao(String nome, String nif, String email) {
        this.nome = nome;
        this.nif = nif;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Plano getPlano() { return plano; }
    public void setPlano(Plano plano) { this.plano = plano; }
}
