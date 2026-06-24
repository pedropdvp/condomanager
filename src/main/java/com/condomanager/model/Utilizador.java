package com.condomanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Conta de acesso ao sistema.
 *
 * <p>Não estende {@code TenantAwareEntity} por dois motivos: o {@code ADMIN_SISTEMA}
 * não pertence a nenhuma empresa ({@code idEmpresa} nulo), e o login por email tem de
 * funcionar antes de existir um tenant no contexto. O isolamento por empresa nas
 * operações de gestão de utilizadores é aplicado explicitamente na camada de serviço
 * (Fase 7).</p>
 */
@Entity
@Table(name = "utilizador")
@Getter
@Setter
@NoArgsConstructor
public class Utilizador extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilizador")
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    /** Tenant a que o utilizador pertence. Nulo para o ADMIN_SISTEMA. */
    @Column(name = "id_empresa")
    private Long idEmpresa;

    /** Condómino associado a esta conta (perfil CONDOMINO), para auto-voto. Opcional. */
    @Column(name = "id_condomino")
    private Long idCondomino;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "utilizador_perfil",
            joinColumns = @JoinColumn(name = "id_utilizador"),
            inverseJoinColumns = @JoinColumn(name = "id_perfil")
    )
    private Set<Perfil> perfis = new HashSet<>();

    public void adicionarPerfil(Perfil perfil) {
        this.perfis.add(perfil);
    }
}
