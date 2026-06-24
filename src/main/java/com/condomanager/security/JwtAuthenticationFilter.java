package com.condomanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Autentica cada pedido a partir do token JWT (cabeçalho {@code Authorization: Bearer}).
 *
 * <p>Além de preencher o {@code SecurityContext}, estabelece o {@link TenantContext}
 * com o {@code id_empresa} do token — substituindo o mecanismo baseado no cabeçalho
 * {@code X-Tenant-Id} usado na Fase 0. O contexto é limpo no fim do pedido.</p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            authenticate(request);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void authenticate(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return;
        }
        String token = header.substring(BEARER_PREFIX.length());
        try {
            Claims claims = jwtService.parse(token);

            Long uid = toLong(claims.get(JwtService.CLAIM_UID));
            Long idEmpresa = toLong(claims.get(JwtService.CLAIM_TENANT));
            String nome = asString(claims.get(JwtService.CLAIM_NOME));
            List<String> roles = asRoles(claims.get(JwtService.CLAIM_ROLES));

            AuthenticatedUser principal = new AuthenticatedUser(uid, nome, claims.getSubject(), idEmpresa, roles);
            var authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

            var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (idEmpresa != null) {
                TenantContext.setTenantId(idEmpresa);
            }
        } catch (JwtException | IllegalArgumentException ex) {
            logger.debug("Token JWT inválido: {}", ex.getMessage());
        }
    }

    private static Long toLong(Object value) {
        return (value instanceof Number number) ? number.longValue() : null;
    }

    private static String asString(Object value) {
        return value != null ? value.toString() : null;
    }

    private static List<String> asRoles(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }
}
