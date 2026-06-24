package com.condomanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Edifício (bloco) de um condomínio. Entidade multi-tenant filha do condomínio.
 */
@Entity
@Table(name = "edificio")
@Getter
@Setter
@NoArgsConstructor
public class Edificio extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_edificio")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "bloco", length = 50)
    private String bloco;

    @Column(name = "numero_pisos")
    private Integer numeroPisos;
}
