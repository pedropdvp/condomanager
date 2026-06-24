package com.condomanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Registo de auditoria. Append-only e <strong>imutável</strong>: sem setters e sem
 * operações de alteração/eliminação na aplicação (ver {@code docs/DOMAIN_MODEL.md}).
 *
 * <p>Não estende {@code TenantAwareEntity} porque os registos têm de poder ser criados
 * sem tenant no contexto (ex.: eventos de sistema) e {@code id_empresa} pode ser nulo.</p>
 */
@Entity
@Table(name = "historico")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Historico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico")
    private Long id;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "utilizador", length = 150)
    private String utilizador;

    @Column(name = "operacao", nullable = false, length = 255)
    private String operacao;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public Historico(Long idEmpresa, String utilizador, String operacao, LocalDateTime dataHora) {
        this.idEmpresa = idEmpresa;
        this.utilizador = utilizador;
        this.operacao = operacao;
        this.dataHora = dataHora;
    }
}
