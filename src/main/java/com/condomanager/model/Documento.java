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
 * Documento de um condomínio (regulamento, ata, contrato, etc.). Entidade multi-tenant.
 *
 * <p>O conteúdo do ficheiro reside no filesystem local; {@code ficheiro} guarda o
 * caminho relativo dentro da pasta de armazenamento.</p>
 */
@Entity
@Table(name = "documento")
@Getter
@Setter
@NoArgsConstructor
public class Documento extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominio condominio;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "tipo", length = 100)
    private String tipo;

    @Column(name = "ficheiro", nullable = false, length = 255)
    private String ficheiro;
}
