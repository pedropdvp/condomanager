package com.condomanager.service;

import com.condomanager.dto.AuthResponse;
import com.condomanager.dto.LoginRequest;
import com.condomanager.dto.UserResponse;
import com.condomanager.security.AuthenticatedUser;
import com.condomanager.security.CustomUserDetails;
import com.condomanager.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Regras de autenticação: valida credenciais e emite o token JWT.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditoriaService auditoriaService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService,
                       AuditoriaService auditoriaService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.auditoriaService = auditoriaService;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        auditoriaService.registarEvento(user.getUsername(), user.getIdEmpresa(), "LOGIN");

        List<String> perfis = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        UserResponse userResponse = new UserResponse(
                user.getId(), user.getNome(), user.getUsername(), user.getIdEmpresa(), perfis);

        return AuthResponse.bearer(token, jwtService.getExpirationMillis(), userResponse);
    }

    public UserResponse currentUser(AuthenticatedUser principal) {
        return new UserResponse(
                principal.id(), principal.nome(), principal.email(),
                principal.idEmpresa(), principal.roles());
    }
}
