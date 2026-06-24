package com.condomanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Condomínio gerido por uma empresa de gestão.
 *
 * <p>Primeira entidade <strong>multi-tenant</strong>: ao estender
 * {@link TenantAwareEntity}, herda a coluna {@code id_empresa}, o preenchimento
 * automático do tenant na persistência e o filtro {@code tenantFilter} que isola
 * as consultas por empresa (ver {@code docs/SAAS_RULES.md}).</p>
 */
@Entity
@Table(name = "condominio")
@Getter
@Setter
@NoArgsConstructor
public class Condominio extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_condominio")
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "morada", length = 255)
    private String morada;

    @Column(name = "orcamento_anual", nullable = false, precision = 12, scale = 2)
    private BigDecimal orcamentoAnual = BigDecimal.ZERO;
}
