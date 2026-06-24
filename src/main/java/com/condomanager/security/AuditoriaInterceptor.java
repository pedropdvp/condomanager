package com.condomanager.security;

import com.condomanager.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * Regista no histórico as operações de escrita (POST/PUT/DELETE/PATCH) bem-sucedidas.
 *
 * <p>O login é auditado explicitamente no {@code AuthService}, por isso os endpoints de
 * autenticação são ignorados aqui.</p>
 */
@Component
public class AuditoriaInterceptor implements HandlerInterceptor {

    private static final Set<String> METODOS_ESCRITA = Set.of("POST", "PUT", "DELETE", "PATCH");

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaInterceptor.class);

    private final AuditoriaService auditoriaService;

    public AuditoriaInterceptor(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (!METODOS_ESCRITA.contains(request.getMethod())) {
            return;
        }
        if (response.getStatus() >= 400) {
            return;
        }
        String uri = request.getRequestURI();
        if (uri.contains("/auth/")) {
            return; // login é auditado no AuthService
        }
        try {
            auditoriaService.registar(request.getMethod() + " " + uri);
        } catch (Exception e) {
            logger.warn("Falha ao registar auditoria de {} {}: {}", request.getMethod(), uri, e.getMessage());
        }
    }
}
