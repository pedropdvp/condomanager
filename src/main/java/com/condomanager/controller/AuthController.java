package com.condomanager.controller;

import com.condomanager.dto.AuthResponse;
import com.condomanager.dto.LoginRequest;
import com.condomanager.dto.RecuperarPasswordDTO;
import com.condomanager.dto.RedefinirPasswordDTO;
import com.condomanager.dto.UserResponse;
import com.condomanager.security.AuthenticatedUser;
import com.condomanager.service.AuthService;
import com.condomanager.service.RecuperacaoPasswordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de autenticação.
 *
 * <p>{@code POST /api/v1/auth/login} é público; {@code GET /api/v1/auth/me} requer
 * um token válido.</p>
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RecuperacaoPasswordService recuperacaoPasswordService;

    public AuthController(AuthService authService, RecuperacaoPasswordService recuperacaoPasswordService) {
        this.authService = authService;
        this.recuperacaoPasswordService = recuperacaoPasswordService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return authService.currentUser(principal);
    }

    /** Solicita a recuperação de password (responde sempre 204, revele ou não a conta). */
    @PostMapping("/recuperar-password")
    public ResponseEntity<Void> recuperar(@Valid @RequestBody RecuperarPasswordDTO dto) {
        recuperacaoPasswordService.solicitar(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/redefinir-password")
    public ResponseEntity<Void> redefinir(@Valid @RequestBody RedefinirPasswordDTO dto) {
        recuperacaoPasswordService.redefinir(dto);
        return ResponseEntity.noContent().build();
    }
}
