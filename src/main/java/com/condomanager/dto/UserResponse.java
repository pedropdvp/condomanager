package com.condomanager.dto;

import java.util.List;

/**
 * Representação pública de um utilizador autenticado.
 */
public record UserResponse(
        Long id,
        String nome,
        String email,
        Long idEmpresa,
        List<String> perfis
) {
}
