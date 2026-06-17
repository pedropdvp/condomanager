package com.condomanager.security;

import com.condomanager.model.enums.Acao;
import com.condomanager.model.enums.Funcionalidade;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Verificador de permissoes granulares, usado em @PreAuthorize:
 *   @PreAuthorize("@permissoes.pode('ATAS','APAGAR')")
 *
 * Le as permissoes do utilizador autenticado (recarregado da BD em cada pedido
 * pelo JwtAuthenticationFilter), pelo que alteracoes de permissoes sao imediatas.
 */
@Component("permissoes")
public class PermissaoChecker {

    public boolean pode(String funcionalidade, String acao) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UtilizadorPrincipal principal)) {
            return false;
        }
        try {
            return principal.temPermissao(Funcionalidade.valueOf(funcionalidade), Acao.valueOf(acao));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
