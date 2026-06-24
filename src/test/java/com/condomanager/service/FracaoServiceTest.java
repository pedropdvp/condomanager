package com.condomanager.service;

import com.condomanager.dto.FracaoCreateDTO;
import com.condomanager.mapper.FracaoMapper;
import com.condomanager.model.Condominio;
import com.condomanager.model.Edificio;
import com.condomanager.model.Fracao;
import com.condomanager.repository.EdificioRepository;
import com.condomanager.repository.FracaoRepository;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FracaoServiceTest {

    @Mock
    private FracaoRepository repository;
    @Mock
    private EdificioRepository edificioRepository;

    private FracaoService service;
    private final FracaoMapper mapper = new FracaoMapper();

    @BeforeEach
    void setUp() {
        service = new FracaoService(repository, edificioRepository, mapper);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    private Edificio edificio(Long edificioId, Long condominioId, Long tenant) {
        Condominio c = new Condominio();
        c.setId(condominioId);
        c.setIdEmpresa(tenant);
        Edificio e = new Edificio();
        e.setId(edificioId);
        e.setIdEmpresa(tenant);
        e.setCondominio(c);
        return e;
    }

    @Test
    void criarComEdificioForaDoCondominioDeveFalhar() {
        TenantContext.setTenantId(5L);
        when(edificioRepository.findByIdAndIdEmpresa(2L, 5L))
                .thenReturn(Optional.of(edificio(2L, 7L, 5L)));

        // condominioId divergente do edifício (8 != 7)
        FracaoCreateDTO dto = new FracaoCreateDTO(8L, 2L, "1A", "T2", new BigDecimal("85.0000"), null);

        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pertence");
    }

    @Test
    void criarValidoDevePersistir() {
        TenantContext.setTenantId(5L);
        when(edificioRepository.findByIdAndIdEmpresa(2L, 5L))
                .thenReturn(Optional.of(edificio(2L, 7L, 5L)));
        when(repository.save(any(Fracao.class))).thenAnswer(inv -> {
            Fracao f = inv.getArgument(0);
            f.setId(1L);
            f.setIdEmpresa(5L);
            return f;
        });

        var response = service.criar(
                new FracaoCreateDTO(7L, 2L, "1A", "T2", new BigDecimal("85.0000"), new BigDecimal("90.50")));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.condominioId()).isEqualTo(7L);
        assertThat(response.edificioId()).isEqualTo(2L);
    }
}
