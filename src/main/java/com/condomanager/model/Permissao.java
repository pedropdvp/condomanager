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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Permissão granular: associa um {@link Perfil} a uma {@link Acao} sobre uma
 * {@link Funcionalidade}. A presença da linha significa "permitido".
 *
 * <p>As permissões são <strong>globais por perfil</strong> (não dependem do tenant);
 * o {@code ADMIN_SISTEMA} tem acesso total por código (não é semeado nesta tabela).</p>
 */
@Entity
@Table(name = "permissao", uniqueConstraints =
        @UniqueConstraint(name = "uk_permissao", columnNames = {"id_perfil", "funcionalidade", "acao"}))
@Getter
@Setter
@NoArgsConstructor
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permissao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "funcionalidade", nullable = false, length = 30)
    private Funcionalidade funcionalidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "acao", nullable = false, length = 20)
    private Acao acao;
}
