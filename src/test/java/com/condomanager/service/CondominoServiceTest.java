package com.condomanager.service;

import com.condomanager.dto.CondominoCreateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.CondominoMapper;
import com.condomanager.model.Condomino;
import com.condomanager.model.Fracao;
import com.condomanager.model.TipoCondomino;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.FracaoRepository;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CondominoServiceTest {

    @Mock
    private CondominoRepository repository;
    @Mock
    private FracaoRepository fracaoRepository;

    private CondominoService service;
    private final CondominoMapper mapper = new CondominoMapper();

    @BeforeEach
    void setUp() {
        service = new CondominoService(repository, fracaoRepository, mapper);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    @Test
    void criarComFracaoDeOutroTenantDeve404() {
        TenantContext.setTenantId(5L);
        when(fracaoRepository.findByIdAndIdEmpresa(99L, 5L)).thenReturn(Optional.empty());

        CondominoCreateDTO dto = new CondominoCreateDTO(
                99L, "Ana", "123456789", "ana@x.pt", "910000000", TipoCondomino.PROPRIETARIO);

        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void criarValidoDevePersistir() {
        TenantContext.setTenantId(5L);
        Fracao fracao = new Fracao();
        fracao.setId(3L);
        fracao.setIdEmpresa(5L);
        when(fracaoRepository.findByIdAndIdEmpresa(3L, 5L)).thenReturn(Optional.of(fracao));
        when(repository.save(any(Condomino.class))).thenAnswer(inv -> {
            Condomino c = inv.getArgument(0);
            c.setId(1L);
            c.setIdEmpresa(5L);
            return c;
        });

        var response = service.criar(new CondominoCreateDTO(
                3L, "Ana", "123456789", "ana@x.pt", "910000000", TipoCondomino.PROPRIETARIO));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.fracaoId()).isEqualTo(3L);
        assertThat(response.tipo()).isEqualTo(TipoCondomino.PROPRIETARIO);
    }
}
