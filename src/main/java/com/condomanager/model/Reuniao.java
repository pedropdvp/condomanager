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

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Reunião (assembleia) de um condomínio. Entidade multi-tenant.
 */
@Entity
@Table(name = "reuniao")
@Getter
@Setter
@NoArgsConstructor
public class Reuniao extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reuniao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "hora")
    private LocalTime hora;

    @Column(name = "local", length = 255)
    private String local;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "ordem_trabalhos")
    private String ordemTrabalhos;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoReuniao estado = EstadoReuniao.AGENDADA;
}
