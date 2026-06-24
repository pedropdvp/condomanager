package com.condomanager.service;

import com.condomanager.dto.OcorrenciaCreateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.OcorrenciaMapper;
import com.condomanager.model.Condominio;
import com.condomanager.model.EstadoOcorrencia;
import com.condomanager.model.Ocorrencia;
import com.condomanager.model.PrioridadeOcorrencia;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.OcorrenciaRepository;
import com.condomanager.repository.UtilizadorRepository;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OcorrenciaServiceTest {

    @Mock private OcorrenciaRepository repository;
    @Mock private CondominioRepository condominioRepository;
    @Mock private CondominoRepository condominoRepository;
    @Mock private UtilizadorRepository utilizadorRepository;

    private OcorrenciaService service;
    private final OcorrenciaMapper mapper = new OcorrenciaMapper();

    @BeforeEach
    void setUp() {
        service = new OcorrenciaService(repository, condominioRepository, condominoRepository,
                utilizadorRepository, mapper);
        TenantContext.setTenantId(2L);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    private Condominio condominio() {
        Condominio c = new Condominio();
        c.setId(7L);
        c.setIdEmpresa(2L);
        return c;
    }

    @Test
    void criarComCondominioDeOutroTenantDeve404() {
        when(condominioRepository.findByIdAndIdEmpresa(99L, 2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.criar(new OcorrenciaCreateDTO(
                99L, null, "Infiltração", "Garagem", PrioridadeOcorrencia.ALTA)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void criarRegistaComEstadoAberta() {
        when(condominioRepository.findByIdAndIdEmpresa(7L, 2L)).thenReturn(Optional.of(condominio()));
        when(repository.save(any(Ocorrencia.class))).thenAnswer(inv -> {
            Ocorrencia o = inv.getArgument(0);
            o.setId(1L);
            o.setIdEmpresa(2L);
            return o;
        });
        var r = service.criar(new OcorrenciaCreateDTO(7L, null, "Infiltração", "Garagem", PrioridadeOcorrencia.ALTA));
        assertThat(r.estado()).isEqualTo(EstadoOcorrencia.ABERTA);
        assertThat(r.prioridade()).isEqualTo(PrioridadeOcorrencia.ALTA);
    }

    @Test
    void naoAlteraEstadoDeOcorrenciaTerminal() {
        Ocorrencia o = new Ocorrencia();
        o.setId(1L);
        o.setIdEmpresa(2L);
        o.setCondominio(condominio());
        o.setEstado(EstadoOcorrencia.CONCLUIDA);
        when(repository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(o));

        assertThatThrownBy(() -> service.alterarEstado(1L, EstadoOcorrencia.EM_EXECUCAO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
