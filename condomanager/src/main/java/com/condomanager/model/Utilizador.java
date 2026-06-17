package com.condomanager.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "utilizador")
public class Utilizador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilizador")
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    /** Hash BCrypt (RNF01). Nunca exposto via API. */
    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false)
    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private EmpresaGestao empresa;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "utilizador_perfil",
            joinColumns = @JoinColumn(name = "id_utilizador"),
            inverseJoinColumns = @JoinColumn(name = "id_perfil")
    )
    private Set<Perfil> perfis = new HashSet<>();

    public Utilizador() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public EmpresaGestao getEmpresa() { return empresa; }
    public void setEmpresa(EmpresaGestao empresa) { this.empresa = empresa; }

    public Set<Perfil> getPerfis() { return perfis; }
    public void setPerfis(Set<Perfil> perfis) { this.perfis = perfis; }
}
