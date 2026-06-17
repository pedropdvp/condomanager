package com.condomanager.service;

import com.condomanager.dto.LoginRequest;
import com.condomanager.dto.LoginResponse;
import com.condomanager.dto.RegistoRequest;
import com.condomanager.dto.RegistoResponse;
import com.condomanager.model.Perfil;
import com.condomanager.model.Utilizador;
import com.condomanager.repository.PerfilRepository;
import com.condomanager.repository.UtilizadorRepository;
import com.condomanager.security.JwtService;
import com.condomanager.security.UtilizadorPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilizadorRepository utilizadorRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UtilizadorRepository utilizadorRepository,
                       PerfilRepository perfilRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilizadorRepository = utilizadorRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse autenticar(LoginRequest pedido) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(pedido.getEmail(), pedido.getPassword()));

        UtilizadorPrincipal principal = (UtilizadorPrincipal) auth.getPrincipal();
        String token = jwtService.gerarToken(principal);

        List<String> perfis = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        return new LoginResponse(token, principal.getUsername(), principal.getUsername(), perfis);
    }

    /**
     * Cria um novo acesso (utilizador) com o perfil indicado.
     * A password e guardada com hash BCrypt (RNF01). Restrito a ADMIN_SISTEMA (ver AuthController).
     */
    @Transactional
    public RegistoResponse registar(RegistoRequest pedido) {
        if (utilizadorRepository.existsByEmail(pedido.getEmail())) {
            throw new IllegalArgumentException("Ja existe um utilizador com este email.");
        }

        Perfil perfil = perfilRepository.findByNome(pedido.getPerfil())
                .orElseThrow(() -> new IllegalArgumentException("Perfil invalido."));

        Utilizador utilizador = new Utilizador();
        utilizador.setNome(pedido.getNome());
        utilizador.setEmail(pedido.getEmail());
        utilizador.setPassword(passwordEncoder.encode(pedido.getPassword()));
        utilizador.setAtivo(true);
        utilizador.setPerfis(Set.of(perfil));

        Utilizador guardado = utilizadorRepository.save(utilizador);

        return new RegistoResponse(guardado.getId(), guardado.getNome(), guardado.getEmail(),
                List.of(perfil.getNome().name()));
    }
}
