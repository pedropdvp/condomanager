package com.condomanager.service;

import com.condomanager.model.Historico;
import com.condomanager.repository.HistoricoRepository;
import com.condomanager.security.AuthenticatedUser;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock private HistoricoRepository repository;
    @Captor private ArgumentCaptor<Historico> captor;

    private AuditoriaService service;

    @BeforeEach
    void setUp() {
        service = new AuditoriaService(repository);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void registarUsaUtilizadorEEmpresaDoContexto() {
        TenantContext.setTenantId(2L);
        AuthenticatedUser principal = new AuthenticatedUser(5L, "Gestor", "gestor@x.pt", 2L,
                List.of("ROLE_GESTOR_EMPRESA"));
        var auth = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_GESTOR_EMPRESA")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        service.registar("POST /api/v1/condominios");

        verify(repository).save(captor.capture());
        Historico h = captor.getValue();
        assertThat(h.getUtilizador()).isEqualTo("gestor@x.pt");
        assertThat(h.getIdEmpresa()).isEqualTo(2L);
        assertThat(h.getOperacao()).isEqualTo("POST /api/v1/condominios");
        assertThat(h.getDataHora()).isNotNull();
    }

    @Test
    void registarSemUtilizadorUsaAnonimo() {
        service.registar("DELETE /api/v1/x/1");

        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getUtilizador()).isEqualTo("anónimo");
        assertThat(captor.getValue().getIdEmpresa()).isNull();
    }

    @Test
    void registarEventoGuardaValoresExplicitos() {
        service.registarEvento("admin@x.pt", 9L, "LOGIN");

        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getOperacao()).isEqualTo("LOGIN");
        assertThat(captor.getValue().getIdEmpresa()).isEqualTo(9L);
    }
}
