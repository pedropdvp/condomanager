package com.condomanager.model;

import com.condomanager.model.enums.NomePerfil;
import jakarta.persistence.*;

@Entity
@Table(name = "perfil")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40, unique = true)
    private NomePerfil nome;

    @Column(length = 150)
    private String descricao;

    public Perfil() {
    }

    public Perfil(NomePerfil nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public NomePerfil getNome() { return nome; }
    public void setNome(NomePerfil nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
