package com.condomanager.model;

import com.condomanager.model.enums.RespostaVoto;
import jakarta.persistence.*;

@Entity
@Table(name = "voto",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_votacao", "id_condomino"}))
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voto")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private RespostaVoto resposta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_votacao")
    private Votacao votacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condomino")
    private Condomino condomino;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RespostaVoto getResposta() { return resposta; }
    public void setResposta(RespostaVoto resposta) { this.resposta = resposta; }

    public Votacao getVotacao() { return votacao; }
    public void setVotacao(Votacao votacao) { this.votacao = votacao; }

    public Condomino getCondomino() { return condomino; }
    public void setCondomino(Condomino condomino) { this.condomino = condomino; }
}
