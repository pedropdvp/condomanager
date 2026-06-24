package com.condomanager.security;

import com.condomanager.model.PerfilTipo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Acesso utilitário ao utilizador autenticado no contexto de segurança.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<AuthenticatedUser> utilizadorAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public static boolean isAdminSistema() {
        return utilizadorAtual()
                .map(user -> user.roles().contains(PerfilTipo.ADMIN_SISTEMA.authority()))
                .orElse(false);
    }
}
