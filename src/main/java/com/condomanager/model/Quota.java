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

import java.math.BigDecimal;

/**
 * Quota mensal emitida a uma fração. Entidade multi-tenant.
 *
 * <p>O {@code valor} é calculado por permilagem sobre o orçamento anual do condomínio
 * (ver {@code docs/LEGAL_RULES.md} §2).</p>
 */
@Entity
@Table(name = "quota")
@Getter
@Setter
@NoArgsConstructor
public class Quota extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quota")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fracao", nullable = false)
    private Fracao fracao;

    @Column(name = "mes", nullable = false)
    private int mes;

    @Column(name = "ano", nullable = false)
    private int ano;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoQuota estado = EstadoQuota.PENDENTE;
}
