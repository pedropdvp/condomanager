package com.condomanager.security;

import com.condomanager.model.Utilizador;
import com.condomanager.repository.UtilizadorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilizadorRepository utilizadorRepository;

    public CustomUserDetailsService(UtilizadorRepository utilizadorRepository) {
        this.utilizadorRepository = utilizadorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilizador u = utilizadorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador nao encontrado: " + email));
        return new UtilizadorPrincipal(u);
    }
}
