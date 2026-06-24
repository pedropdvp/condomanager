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
 * Condómino (proprietário ou inquilino) associado a uma fração. Entidade multi-tenant.
 */
@Entity
@Table(name = "condomino")
@Getter
@Setter
@NoArgsConstructor
public class Condomino extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_condomino")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fracao", nullable = false)
    private Fracao fracao;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "nif", length = 20)
    private String nif;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "telefone", length = 30)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoCondomino tipo;
}
