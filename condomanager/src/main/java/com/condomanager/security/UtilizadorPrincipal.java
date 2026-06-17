package com.condomanager.security;

import com.condomanager.model.Utilizador;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Adapta a entidade Utilizador ao contrato UserDetails do Spring Security.
 * Expoe o id da empresa para suporte ao isolamento multi-tenant.
 */
public class UtilizadorPrincipal implements UserDetails {

    private final Utilizador utilizador;

    public UtilizadorPrincipal(Utilizador utilizador) {
        this.utilizador = utilizador;
    }

    public Long getId() {
        return utilizador.getId();
    }

    public Long getEmpresaId() {
        return utilizador.getEmpresa() != null ? utilizador.getEmpresa().getId() : null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return utilizador.getPerfis().stream()
                .map(p -> new SimpleGrantedAuthority("ROLE_" + p.getNome().name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return utilizador.getPassword();
    }

    @Override
    public String getUsername() {
        return utilizador.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return utilizador.isAtivo();
    }
}
