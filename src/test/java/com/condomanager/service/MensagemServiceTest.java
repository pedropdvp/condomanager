package com.condomanager.service;

import com.condomanager.dto.MensagemCreateDTO;
import com.condomanager.dto.MensagemResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.MensagemMapper;
import com.condomanager.model.Mensagem;
import com.condomanager.model.TipoMensagem;
import com.condomanager.model.Utilizador;
import com.condomanager.repository.MensagemRepository;
import com.condomanager.repository.UtilizadorRepository;
import com.condomanager.security.AuthenticatedUser;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MensagemServiceTest {

    @Mock private MensagemRepository repository;
    @Mock private UtilizadorRepository utilizadorRepository;

    private MensagemService service;
    private final MensagemMapper mapper = new MensagemMapper();

    @BeforeEach
    void setUp() {
        service = new MensagemService(repository, utilizadorRepository, mapper);
        TenantContext.setTenantId(2L);
        AuthenticatedUser principal = new AuthenticatedUser(10L, "Gestor", "g@x.pt", 2L, List.of("ROLE_GESTOR_EMPRESA"));
        var auth = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_GESTOR_EMPRESA")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(utilizadorRepository.findById(10L)).thenReturn(Optional.of(utilizador(10L, 2L)));
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    private Utilizador utilizador(Long id, Long idEmpresa) {
        Utilizador u = new Utilizador();
        u.setId(id);
        u.setNome("User " + id);
        u.setIdEmpresa(idEmpresa);
        return u;
    }

    @Test
    void individualSemDestinoFalha() {
        MensagemCreateDTO dto = new MensagemCreateDTO(TipoMensagem.INDIVIDUAL, null, null, "Olá", "Corpo");
        assertThatThrownBy(() -> service.enviar(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void individualParaDestinoDeOutraEmpresaDeve404() {
        when(utilizadorRepository.findById(99L)).thenReturn(Optional.of(utilizador(99L, 3L))); // outra empresa
        MensagemCreateDTO dto = new MensagemCreateDTO(TipoMensagem.INDIVIDUAL, 99L, null, "Olá", "Corpo");
        assertThatThrownBy(() -> service.enviar(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void broadcastNaoTemDestino() {
        when(repository.save(any(Mensagem.class))).thenAnswer(inv -> {
            Mensagem m = inv.getArgument(0);
            m.setId(1L);
            m.setIdEmpresa(2L);
            return m;
        });
        MensagemResponse r = service.enviar(new MensagemCreateDTO(TipoMensagem.BROADCAST, null, null, "Aviso", "A todos"));
        assertThat(r.id()).isEqualTo(1L);
        assertThat(r.destinoId()).isNull();
        assertThat(r.origemId()).isEqualTo(10L);
    }
}
