package com.condomanager.service;

import com.condomanager.dto.UtilizadorCreateDTO;
import com.condomanager.dto.UtilizadorResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.UtilizadorMapper;
import com.condomanager.model.Perfil;
import com.condomanager.model.PerfilTipo;
import com.condomanager.model.Utilizador;
import com.condomanager.repository.EmpresaGestaoRepository;
import com.condomanager.repository.PerfilRepository;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UtilizadorServiceTest {

    @Mock
    private UtilizadorRepository repository;
    @Mock
    private PerfilRepository perfilRepository;
    @Mock
    private EmpresaGestaoRepository empresaRepository;
    @Mock
    private com.condomanager.repository.CondominoRepository condominoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UtilizadorService service;
    private final UtilizadorMapper mapper = new UtilizadorMapper();

    @BeforeEach
    void setUp() {
        service = new UtilizadorService(repository, perfilRepository, empresaRepository,
                condominoRepository, mapper, passwordEncoder);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void gestorNaoPodeAtribuirAdminSistema() {
        autenticarGestor(2L);
        when(repository.existsByEmail(any())).thenReturn(false);

        UtilizadorCreateDTO dto = new UtilizadorCreateDTO(
                "Mau", "mau@x.pt", "secret1", Set.of(PerfilTipo.ADMIN_SISTEMA.name()), null);

        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void gestorCriaUtilizadorNaSuaEmpresa() {
        autenticarGestor(2L);
        when(repository.existsByEmail(any())).thenReturn(false);
        when(perfilRepository.findByNome("FUNCIONARIO")).thenReturn(Optional.of(perfil("FUNCIONARIO")));
        when(passwordEncoder.encode(any())).thenReturn("hash");
        when(repository.save(any(Utilizador.class))).thenAnswer(inv -> {
            Utilizador u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        UtilizadorResponse r = service.criar(new UtilizadorCreateDTO(
                "Rui", "rui@x.pt", "secret1", Set.of("FUNCIONARIO"), 999L)); // idEmpresa pedido é ignorado

        assertThat(r.id()).isEqualTo(10L);
        assertThat(r.idEmpresa()).isEqualTo(2L); // forçado ao tenant do gestor
        assertThat(r.perfis()).containsExactly("FUNCIONARIO");
    }

    @Test
    void gestorNaoAcedeUtilizadorDeOutraEmpresa() {
        autenticarGestor(2L);
        Utilizador outro = new Utilizador();
        outro.setId(9L);
        outro.setIdEmpresa(3L);
        when(repository.findById(9L)).thenReturn(Optional.of(outro));

        assertThatThrownBy(() -> service.obterPorId(9L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private Perfil perfil(String nome) {
        Perfil p = new Perfil();
        p.setNome(nome);
        return p;
    }

    private void autenticarGestor(Long idEmpresa) {
        TenantContext.setTenantId(idEmpresa);
        AuthenticatedUser principal = new AuthenticatedUser(
                1L, "Gestor", "g@x.pt", idEmpresa, List.of("ROLE_GESTOR_EMPRESA"));
        var auth = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_GESTOR_EMPRESA")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
