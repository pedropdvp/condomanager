package com.condomanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Geracao e validacao de tokens JWT (assinatura HMAC-SHA).
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${condomanager.jwt.secret}") String secret,
            @Value("${condomanager.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String gerarToken(UtilizadorPrincipal principal) {
        List<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        Date agora = new Date();
        Date validade = new Date(agora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("uid", principal.getId())
                .claim("empresaId", principal.getEmpresaId())
                .claim("roles", roles)
                .issuedAt(agora)
                .expiration(validade)
                .signWith(key)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public Long extrairEmpresaId(String token) {
        Object v = extrairTodasClaims(token).get("empresaId");
        return v == null ? null : Long.valueOf(v.toString());
    }

    public boolean tokenValido(String token) {
        try {
            Date exp = extrairClaim(token, Claims::getExpiration);
            return exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private <T> T extrairClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extrairTodasClaims(token));
    }

    private Claims extrairTodasClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
