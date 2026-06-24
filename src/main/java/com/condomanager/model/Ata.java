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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

/**
 * Ata — documento oficial de uma reunião/assembleia. Entidade multi-tenant.
 *
 * <p>Nesta fase {@code idReuniao} é apenas uma referência opcional (sem associação JPA);
 * a ligação a {@code Reuniao} será concretizada na Fase 12.</p>
 */
@Entity
@Table(name = "ata")
@Getter
@Setter
@NoArgsConstructor
public class Ata extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ata")
    private Long id;

    @Column(name = "id_reuniao")
    private Long idReuniao;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data_reuniao", nullable = false)
    private LocalDate dataReuniao;

    /** Caminho relativo do ficheiro anexo (opcional). */
    @Column(name = "ficheiro", length = 255)
    private String ficheiro;
}
