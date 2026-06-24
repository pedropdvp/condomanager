package com.condomanager.security;

import com.condomanager.model.Utilizador;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapta {@link Utilizador} ao contrato {@link UserDetails} do Spring Security.
 * Expõe também o {@code idEmpresa} (tenant) para alimentar o {@link TenantContext}.
 */
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String nome;
    private final String email;
    private final String password;
    private final boolean ativo;
    private final Long idEmpresa;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(Utilizador utilizador) {
        this.id = utilizador.getId();
        this.nome = utilizador.getNome();
        this.email = utilizador.getEmail();
        this.password = utilizador.getPassword();
        this.ativo = utilizador.isAtivo();
        this.idEmpresa = utilizador.getIdEmpresa();
        this.authorities = utilizador.getPerfis().stream()
                .map(perfil -> new SimpleGrantedAuthority(perfil.getAuthority()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return ativo;
    }
}
