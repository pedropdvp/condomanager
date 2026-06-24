package com.condomanager.service;

import com.condomanager.dto.AtaCreateDTO;
import com.condomanager.dto.AtaResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.AtaMapper;
import com.condomanager.model.Ata;
import com.condomanager.repository.AtaRepository;
import com.condomanager.repository.ReuniaoRepository;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtaServiceTest {

    @Mock
    private AtaRepository repository;
    @Mock
    private ReuniaoRepository reuniaoRepository;
    @Mock
    private FileStorageService storage;

    private AtaService service;
    private final AtaMapper mapper = new AtaMapper();

    @BeforeEach
    void setUp() {
        service = new AtaService(repository, reuniaoRepository, storage, mapper);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    @Test
    void criarSemTenantDeveSerNegado() {
        TenantContext.clear();
        assertThatThrownBy(() -> service.criar(
                new AtaCreateDTO(null, "Assembleia Geral", "Pontos...", LocalDate.now())))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void criarComTenantDevePersistir() {
        TenantContext.setTenantId(2L);
        when(repository.save(any(Ata.class))).thenAnswer(inv -> {
            Ata a = inv.getArgument(0);
            a.setId(1L);
            a.setIdEmpresa(2L);
            return a;
        });

        AtaResponse r = service.criar(new AtaCreateDTO(null, "Assembleia Geral", "Pontos...", LocalDate.now()));

        assertThat(r.id()).isEqualTo(1L);
        assertThat(r.titulo()).isEqualTo("Assembleia Geral");
        assertThat(r.temFicheiro()).isFalse();
    }

    @Test
    void obterDeOutroTenantDeve404() {
        TenantContext.setTenantId(2L);
        when(repository.findByIdAndIdEmpresa(9L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obterPorId(9L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
