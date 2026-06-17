package com.condomanager.model;

import com.condomanager.model.enums.TipoCondomino;
import jakarta.persistence.*;

@Entity
@Table(name = "condomino")
public class Condomino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_condomino")
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 20)
    private String nif;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoCondomino tipo = TipoCondomino.PROPRIETARIO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fracao")
    private Fracao fracao;

    public Condomino() {
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

    public TipoCondomino getTipo() { return tipo; }
    public void setTipo(TipoCondomino tipo) { this.tipo = tipo; }

    public Fracao getFracao() { return fracao; }
    public void setFracao(Fracao fracao) { this.fracao = fracao; }
}
