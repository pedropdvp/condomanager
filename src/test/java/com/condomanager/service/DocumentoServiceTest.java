package com.condomanager.service;

import com.condomanager.dto.DocumentoResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.DocumentoMapper;
import com.condomanager.model.Condominio;
import com.condomanager.model.Documento;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.DocumentoRepository;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceTest {

    @Mock
    private DocumentoRepository repository;
    @Mock
    private CondominioRepository condominioRepository;
    @Mock
    private FileStorageService storage;

    private DocumentoService service;
    private final DocumentoMapper mapper = new DocumentoMapper();

    @BeforeEach
    void setUp() {
        service = new DocumentoService(repository, condominioRepository, storage, mapper);
        TenantContext.setTenantId(2L);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    private MockMultipartFile ficheiro() {
        return new MockMultipartFile("ficheiro", "regulamento.pdf",
                "application/pdf", "conteudo".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void uploadComCondominioDeOutroTenantNaoGuardaFicheiro() {
        when(condominioRepository.findByIdAndIdEmpresa(99L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(99L, "Regulamento", "PDF", ficheiro()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(storage, never()).guardar(any(), any());
    }

    @Test
    void uploadValidoGuardaFicheiroEMetadados() {
        Condominio cond = new Condominio();
        cond.setId(7L);
        cond.setIdEmpresa(2L);
        when(condominioRepository.findByIdAndIdEmpresa(7L, 2L)).thenReturn(Optional.of(cond));
        when(storage.guardar(any(), eq(2L))).thenReturn("2/uuid.pdf");
        when(repository.save(any(Documento.class))).thenAnswer(inv -> {
            Documento d = inv.getArgument(0);
            d.setId(1L);
            d.setIdEmpresa(2L);
            return d;
        });

        DocumentoResponse r = service.criar(7L, "Regulamento", "PDF", ficheiro());

        assertThat(r.id()).isEqualTo(1L);
        assertThat(r.condominioId()).isEqualTo(7L);
        assertThat(r.nome()).isEqualTo("Regulamento");
        verify(storage).guardar(any(), eq(2L));
    }
}
