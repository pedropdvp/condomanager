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

import java.math.BigDecimal;

/**
 * Fração autónoma (apartamento, loja, garagem, arrecadação). Entidade multi-tenant.
 *
 * <p>A {@code permilagem} é expressa em milésimos (0–1000) e é a base do rateio de
 * quotas e do peso de voto (ver {@code docs/LEGAL_RULES.md} §2).</p>
 */
@Entity
@Table(name = "fracao")
@Getter
@Setter
@NoArgsConstructor
public class Fracao extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fracao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_edificio", nullable = false)
    private Edificio edificio;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Column(name = "tipologia", length = 20)
    private String tipologia;

    @Column(name = "permilagem", nullable = false, precision = 8, scale = 4)
    private BigDecimal permilagem = BigDecimal.ZERO;

    @Column(name = "area_m2", precision = 8, scale = 2)
    private BigDecimal areaM2;
}
