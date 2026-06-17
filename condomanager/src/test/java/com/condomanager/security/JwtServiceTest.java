package com.condomanager.security;

import com.condomanager.model.EmpresaGestao;
import com.condomanager.model.Perfil;
import com.condomanager.model.Utilizador;
import com.condomanager.model.enums.NomePerfil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "chave-de-teste-com-mais-de-32-bytes-para-hmac-sha!!", 3600000L);
    }

    private UtilizadorPrincipal principal() {
        EmpresaGestao empresa = new EmpresaGestao("E", "1", "e@x.pt");
        empresa.setId(42L);
        Utilizador u = new Utilizador();
        u.setId(1L);
        u.setEmail("user@x.pt");
        u.setPassword("hash");
        u.setEmpresa(empresa);
        u.setPerfis(Set.of(new Perfil(NomePerfil.GESTOR, "Gestor")));
        return new UtilizadorPrincipal(u);
    }

    @Test
    void gerarTokenEExtrairDados() {
        String token = jwtService.gerarToken(principal());

        assertThat(jwtService.tokenValido(token)).isTrue();
        assertThat(jwtService.extrairEmail(token)).isEqualTo("user@x.pt");
        assertThat(jwtService.extrairEmpresaId(token)).isEqualTo(42L);
    }

    @Test
    void tokenAdulteradoEInvalido() {
        String token = jwtService.gerarToken(principal());
        String adulterado = token.substring(0, token.length() - 3) + "abc";

        assertThat(jwtService.tokenValido(adulterado)).isFalse();
    }
}
