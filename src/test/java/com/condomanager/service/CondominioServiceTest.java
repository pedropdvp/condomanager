package com.condomanager.service;

import com.condomanager.dto.CondominioCreateDTO;
import com.condomanager.dto.CondominioResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.CondominioMapper;
import com.condomanager.model.Condominio;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CondominioServiceTest {

    @Mock
    private CondominioRepository repository;

    private CondominioService service;
    private final CondominioMapper mapper = new CondominioMapper();

    @BeforeEach
    void setUp() {
        service = new CondominioService(repository, mapper);
    }

    @AfterEach
    void limparTenant() {
        TenantContext.clear();
    }

    @Test
    void criarSemTenantDeveSerNegado() {
        TenantContext.clear();
        CondominioCreateDTO dto = new CondominioCreateDTO("Cond", "Rua X", BigDecimal.TEN);

        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void criarComTenantDevePersistir() {
        TenantContext.setTenantId(5L);
        when(repository.save(any(Condominio.class))).thenAnswer(invocation -> {
            Condominio c = invocation.getArgument(0);
            c.setId(1L);
            c.setIdEmpresa(5L); // simula o TenantEntityListener
            return c;
        });

        CondominioResponse response = service.criar(
                new CondominioCreateDTO("Cond Central", "Rua A", new BigDecimal("12000.00")));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.idEmpresa()).isEqualTo(5L);
    }

    @Test
    void obterDeOutroTenantDeveSer404() {
        TenantContext.setTenantId(5L);
        when(repository.findByIdAndIdEmpresa(99L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obterPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void obterDoProprioTenantDeveDevolver() {
        TenantContext.setTenantId(5L);
        Condominio c = new Condominio();
        c.setId(2L);
        c.setNome("Meu Cond");
        c.setIdEmpresa(5L);
        when(repository.findByIdAndIdEmpresa(2L, 5L)).thenReturn(Optional.of(c));

        assertThat(service.obterPorId(2L).nome()).isEqualTo("Meu Cond");
    }
}
