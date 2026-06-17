package com.condomanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documento")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    /** Tipo: REGULAMENTO, CONTRATO, ORCAMENTO, ATA, FATURA, APOLICE. */
    @Column(length = 30)
    private String tipo;

    /** Caminho/refencia do ficheiro armazenado. */
    @Column(length = 255)
    private String ficheiro;

    @Column(name = "data_upload")
    private LocalDateTime dataUpload = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio")
    private Condominio condominio;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFicheiro() { return ficheiro; }
    public void setFicheiro(String ficheiro) { this.ficheiro = ficheiro; }

    public LocalDateTime getDataUpload() { return dataUpload; }
    public void setDataUpload(LocalDateTime dataUpload) { this.dataUpload = dataUpload; }

    public Condominio getCondominio() { return condominio; }
    public void setCondominio(Condominio condominio) { this.condominio = condominio; }
}
