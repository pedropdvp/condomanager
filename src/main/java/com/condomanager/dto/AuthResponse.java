package com.condomanager.dto;

/**
 * Resposta de autenticação bem-sucedida.
 *
 * @param token      o JWT de acesso
 * @param tipo       o esquema do token (Bearer)
 * @param expiraEmMs validade do token em milissegundos
 * @param utilizador dados do utilizador autenticado
 */
public record AuthResponse(
        String token,
        String tipo,
        long expiraEmMs,
        UserResponse utilizador
) {
    public static AuthResponse bearer(String token, long expiraEmMs, UserResponse utilizador) {
        return new AuthResponse(token, "Bearer", expiraEmMs, utilizador);
    }
}
