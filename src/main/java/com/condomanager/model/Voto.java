package com.condomanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Voto de um condómino numa votação. O peso é dado pela permilagem da fração do
 * condómino (calculado na contagem).
 */
@Entity
@Table(name = "voto")
@Getter
@Setter
@NoArgsConstructor
public class Voto extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_voto")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_votacao", nullable = false)
    private Votacao votacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condomino", nullable = false)
    private Condomino condomino;

    @Enumerated(EnumType.STRING)
    @Column(name = "resposta", nullable = false, length = 20)
    private RespostaVoto resposta;
}
