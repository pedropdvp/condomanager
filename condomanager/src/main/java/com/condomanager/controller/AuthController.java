package com.condomanager.controller;

import com.condomanager.dto.LoginRequest;
import com.condomanager.dto.LoginResponse;
import com.condomanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
}
