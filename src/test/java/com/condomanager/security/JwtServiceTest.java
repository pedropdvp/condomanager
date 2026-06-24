package com.condomanager.security;

import com.condomanager.model.Perfil;
import com.condomanager.model.PerfilTipo;
import com.condomanager.model.Utilizador;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários do {@link JwtService} (sem necessidade de base de dados).
 */
class JwtServiceTest {

    private static final String SECRET = "test-secret-com-32-ou-mais-caracteres-abcdefghij";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3_600_000L);
    }

    @Test
    void deveGerarTokenComOsClaimsCorretos() {
        CustomUserDetails user = new CustomUserDetails(novoGestor());

        String token = jwtService.generateToken(user);
        Claims claims = jwtService.parse(token);

        assertThat(claims.getSubject()).isEqualTo("gestor@empresa.pt");
        assertThat(((Number) claims.get(JwtService.CLAIM_UID)).longValue()).isEqualTo(10L);
        assertThat(((Number) claims.get(JwtService.CLAIM_TENANT)).longValue()).isEqualTo(5L);
        assertThat(claims.get(JwtService.CLAIM_NOME)).isEqualTo("Gestor Teste");
        assertThat((List<?>) claims.get(JwtService.CLAIM_ROLES))
                .extracting(Object::toString)
                .containsExactly("ROLE_GESTOR_EMPRESA");
    }

    @Test
    void tokenDeAdminSistemaNaoTransportaTenant() {
        Utilizador admin = new Utilizador();
        admin.setId(1L);
        admin.setNome("Admin");
        admin.setEmail("admin@condomanager.local");
        admin.setPassword("x");
        admin.setIdEmpresa(null);
        admin.setPerfis(Set.of(perfil(PerfilTipo.ADMIN_SISTEMA)));

        Claims claims = jwtService.parse(jwtService.generateToken(new CustomUserDetails(admin)));

        assertThat(claims.get(JwtService.CLAIM_TENANT)).isNull();
    }

    private Utilizador novoGestor() {
        Utilizador gestor = new Utilizador();
        gestor.setId(10L);
        gestor.setNome("Gestor Teste");
        gestor.setEmail("gestor@empresa.pt");
        gestor.setPassword("x");
        gestor.setIdEmpresa(5L);
        gestor.setPerfis(Set.of(perfil(PerfilTipo.GESTOR_EMPRESA)));
        return gestor;
    }

    private Perfil perfil(PerfilTipo tipo) {
        Perfil perfil = new Perfil();
        perfil.setNome(tipo.name());
        return perfil;
    }
}
