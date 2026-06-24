package com.condomanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Empresa de gestão de condomínios — o agregado raiz e o <strong>tenant</strong> da
 * plataforma SaaS (ver {@code docs/DOMAIN_MODEL.md} e {@code docs/SAAS_RULES.md}).
 *
 * <p>Por ser a própria raiz do tenant, não estende {@code TenantAwareEntity}: o seu
 * identificador <em>é</em> o {@code id_empresa}. O isolamento é garantido por RBAC e
 * por verificação de posse na camada de serviço.</p>
 */
@Entity
@Table(name = "empresa_gestao")
@Getter
@Setter
@NoArgsConstructor
public class EmpresaGestao extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "nif", nullable = false, unique = true, length = 20)
    private String nif;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "telefone", length = 30)
    private String telefone;

    @Column(name = "morada", length = 255)
    private String morada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEmpresa estado = EstadoEmpresa.ATIVO;
}
