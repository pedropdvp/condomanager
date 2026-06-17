package com.condomanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensagem")
    private Long id;

    @Column(nullable = false, length = 150)
    private String assunto;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio = LocalDateTime.now();

    /** Destino: TODOS, GRUPO ou um condomino especifico. */
    @Column(length = 20)
    private String destino = "TODOS";

    /** Preenchido quando destino e um condomino especifico. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condomino")
    private Condomino condomino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio")
    private Condominio condominio;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public Condomino getCondomino() { return condomino; }
    public void setCondomino(Condomino condomino) { this.condomino = condomino; }

    public Condominio getCondominio() { return condominio; }
    public void setCondominio(Condominio condominio) { this.condominio = condominio; }
}
