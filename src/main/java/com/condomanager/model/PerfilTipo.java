package com.condomanager.model;

/**
 * Perfis base do sistema (RBAC), conforme {@code docs/DATABASE_SCHEMA.md}.
 *
 * <p>Cada perfil corresponde a uma autoridade Spring Security no formato
 * {@code ROLE_<nome>} (ex.: {@code ROLE_ADMIN_SISTEMA}).</p>
 */
public enum PerfilTipo {
    ADMIN_SISTEMA,
    GESTOR_EMPRESA,
    FUNCIONARIO,
    ADMIN_CONDOMINIO,
    CONDOMINO;

    public static final String PREFIXO_ROLE = "ROLE_";

    /** Nome da autoridade Spring Security correspondente. */
    public String authority() {
        return PREFIXO_ROLE + name();
    }
}
