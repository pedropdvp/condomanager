package com.condomanager.controller;

import com.condomanager.dto.LoginRequest;
import com.condomanager.dto.LoginResponse;
import com.condomanager.dto.RegistoRequest;
import com.condomanager.dto.RegistoResponse;
import com.condomanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest pedido) {
        return ResponseEntity.ok(authService.autenticar(pedido));
    }

    /** Criacao de um novo acesso. Restrito a quem tem CRIAR em UTILIZADORES. */
    @PostMapping("/registo")
    @PreAuthorize("@permissoes.pode('UTILIZADORES','CRIAR')")
    public ResponseEntity<RegistoResponse> registar(@Valid @RequestBody RegistoRequest pedido) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registar(pedido));
    }
}
