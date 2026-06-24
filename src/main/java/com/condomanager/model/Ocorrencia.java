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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Ocorrência (pedido ou incidente) de um condomínio. Entidade multi-tenant.
 */
@Entity
@Table(name = "ocorrencia")
@Getter
@Setter
@NoArgsConstructor
public class Ocorrencia extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ocorrencia")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    /** Condómino que reportou (opcional). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condomino")
    private Condomino condomino;

    /** Utilizador responsável pela resolução (atribuição). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilizador_responsavel")
    private Utilizador responsavel;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "descricao")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoOcorrencia estado = EstadoOcorrencia.ABERTA;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade", nullable = false, length = 20)
    private PrioridadeOcorrencia prioridade = PrioridadeOcorrencia.MEDIA;
}
