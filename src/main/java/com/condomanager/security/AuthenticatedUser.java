package com.condomanager.security;

import java.util.List;

/**
 * Principal autenticado, reconstruído a partir dos claims do JWT em cada pedido.
 * Evita um acesso à base de dados por pedido.
 *
 * @param id        identificador do utilizador
 * @param nome      nome do utilizador
 * @param email     email (subject do token)
 * @param idEmpresa tenant; nulo para o ADMIN_SISTEMA
 * @param roles     autoridades (ex.: {@code ROLE_GESTOR_EMPRESA})
 */
public record AuthenticatedUser(
        Long id,
        String nome,
        String email,
        Long idEmpresa,
        List<String> roles
) {
}
