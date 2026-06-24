package com.condomanager.service;

import com.condomanager.dto.EmpresaCreateDTO;
import com.condomanager.dto.EmpresaResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.EmpresaMapper;
import com.condomanager.model.EmpresaGestao;
import com.condomanager.repository.EmpresaGestaoRepository;
import com.condomanager.security.AuthenticatedUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class EmpresaGestaoServiceTest {

    @Mock
    private EmpresaGestaoRepository repository;

    private EmpresaGestaoService service;

    // Usa o mapper real (sem lógica externa) em vez de um mock.
    private final EmpresaMapper mapper = new EmpresaMapper();

    @BeforeEach
    void setUp() {
        service = new EmpresaGestaoService(repository, mapper);
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void criarDeveRejeitarNifDuplicado() {
        when(repository.existsByNif("123")).thenReturn(true);

        EmpresaCreateDTO dto = new EmpresaCreateDTO("Acme", "123", "a@a.pt", null, null);

        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NIF");
    }

    @Test
    void criarDevePersistirQuandoValido() {
        when(repository.existsByNif(any())).thenReturn(false);
        when(repository.existsByEmail(any())).thenReturn(false);
        when(repository.save(any(EmpresaGestao.class))).thenAnswer(invocation -> {
            EmpresaGestao e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        EmpresaResponse response = service.criar(
                new EmpresaCreateDTO("Acme", "500100200", "geral@acme.pt", "910000000", "Lisboa"));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Acme");
    }

    @Test
    void gestorNaoPodeAcederAEmpresaDeOutroTenant() {
        autenticarComo(99L, "ROLE_GESTOR_EMPRESA");

        // Tenta aceder à empresa 7 enquanto pertence à 99
        assertThatThrownBy(() -> service.obterPorId(7L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void gestorPodeAcederAsuaPropriaEmpresa() {
        autenticarComo(7L, "ROLE_GESTOR_EMPRESA");
        EmpresaGestao empresa = new EmpresaGestao();
        empresa.setId(7L);
        empresa.setNome("Minha Empresa");
        when(repository.findById(7L)).thenReturn(Optional.of(empresa));

        EmpresaResponse response = service.obterPorId(7L);

        assertThat(response.id()).isEqualTo(7L);
    }

    private void autenticarComo(Long idEmpresa, String role) {
        AuthenticatedUser principal = new AuthenticatedUser(1L, "User", "u@u.pt", idEmpresa, List.of(role));
        var auth = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
