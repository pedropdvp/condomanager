package com.condomanager.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Mensagem interna de comunicação. Entidade multi-tenant.
 *
 * <p>Para {@link TipoMensagem#BROADCAST} o {@code destino} é nulo (mensagem para todos
 * os utilizadores da empresa).</p>
 */
@Entity
@Table(name = "mensagem")
@Getter
@Setter
@NoArgsConstructor
public class Mensagem extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensagem")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMensagem tipo;

    @Column(name = "assunto", nullable = false, length = 200)
    private String assunto;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "conteudo")
    private String conteudo;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_utilizador_origem", nullable = false)
    private Utilizador origem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilizador_destino")
    private Utilizador destino;

    /** Estado de leitura (aplicável a mensagens individuais). */
    @Column(name = "lida", nullable = false)
    private boolean lida = false;

    /** Destinatários explícitos de uma mensagem de GRUPO (ids de utilizador). */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "mensagem_destinatario", joinColumns = @JoinColumn(name = "id_mensagem"))
    @Column(name = "id_utilizador")
    private Set<Long> destinatarios = new HashSet<>();
}
