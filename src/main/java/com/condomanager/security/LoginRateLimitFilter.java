package com.condomanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limita as tentativas de login por IP (proteção básica contra brute-force):
 * no máximo {@value #LIMITE} pedidos a {@code POST /api/v1/auth/login} por janela
 * de {@value #JANELA_MS} ms; acima disso devolve HTTP 429.
 */
@Component
@Order(1)
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final int LIMITE = 10;
    private static final long JANELA_MS = 60_000;
    private static final int MAX_ENTRADAS = 50_000;

    /** ip -> [inicioJanelaMillis, contagem] */
    private final Map<String, long[]> tentativas = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if ("POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().endsWith("/api/v1/auth/login")) {
            long agora = System.currentTimeMillis();
            long[] janela = tentativas.compute(clientIp(request), (k, v) -> {
                if (v == null || agora - v[0] > JANELA_MS) {
                    return new long[]{agora, 1};
                }
                v[1]++;
                return v;
            });
            if (tentativas.size() > MAX_ENTRADAS) {
                tentativas.clear();
            }
            if (janela[1] > LIMITE) {
                response.setStatus(429);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                        "{\"message\":\"Demasiadas tentativas de login. Tente novamente dentro de 1 minuto.\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : request.getRemoteAddr();
    }
}
