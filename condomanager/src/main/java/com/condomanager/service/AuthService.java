package com.condomanager.service;

import com.condomanager.dto.LoginRequest;
import com.condomanager.dto.LoginResponse;
import com.condomanager.security.JwtService;
import com.condomanager.security.UtilizadorPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
}
