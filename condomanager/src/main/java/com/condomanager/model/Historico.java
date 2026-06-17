package com.condomanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Registo de auditoria (RF17 / RNF09).
 */
@Entity
@Table(name = "historico")
public class Historico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico")
    private Long id;

    @Column(length = 150)
    private String utilizador;

    @Column(length = 255)
    private String operacao;

    @Column(name = "data_hora")
    private LocalDateTime dataHora = LocalDateTime.now();

    public Historico() {
    }

    public Historico(String utilizador, String operacao) {
        this.utilizador = utilizador;
        this.operacao = operacao;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUtilizador() { return utilizador; }
    public void setUtilizador(String utilizador) { this.utilizador = utilizador; }

    public String getOperacao() { return operacao; }
    public void setOperacao(String operacao) { this.operacao = operacao; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
