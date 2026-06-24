package com.condomanager.service;

import com.condomanager.dto.DashboardResponse;
import com.condomanager.model.EstadoOcorrencia;
import com.condomanager.model.EstadoQuota;
import com.condomanager.model.EstadoReuniao;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.DespesaRepository;
import com.condomanager.repository.EdificioRepository;
import com.condomanager.repository.FracaoRepository;
import com.condomanager.repository.OcorrenciaRepository;
import com.condomanager.repository.QuotaRepository;
import com.condomanager.repository.ReuniaoRepository;
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
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardServiceTest {

    @Mock private CondominioRepository condominioRepository;
    @Mock private EdificioRepository edificioRepository;
    @Mock private FracaoRepository fracaoRepository;
    @Mock private CondominoRepository condominoRepository;
    @Mock private UtilizadorRepository utilizadorRepository;
    @Mock private QuotaRepository quotaRepository;
    @Mock private DespesaRepository despesaRepository;
    @Mock private OcorrenciaRepository ocorrenciaRepository;
    @Mock private ReuniaoRepository reuniaoRepository;

    private DashboardService service;

    @BeforeEach
    void setUp() {
        service = new DashboardService(condominioRepository, edificioRepository, fracaoRepository,
                condominoRepository, utilizadorRepository, quotaRepository, despesaRepository,
                ocorrenciaRepository, reuniaoRepository);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    @Test
    void overviewSemTenantEhNegado() {
        TenantContext.clear();
        assertThatThrownBy(() -> service.overview()).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void overviewAgregaIndicadores() {
        TenantContext.setTenantId(2L);
        when(condominioRepository.count()).thenReturn(1L);
        when(edificioRepository.count()).thenReturn(1L);
        when(fracaoRepository.count()).thenReturn(1L);
        when(condominoRepository.count()).thenReturn(1L);
        when(utilizadorRepository.countByIdEmpresa(2L)).thenReturn(2L);
        when(quotaRepository.countByEstado(any())).thenReturn(0L);
        when(quotaRepository.countByEstado(EstadoQuota.PAGO)).thenReturn(1L);
        when(quotaRepository.somaValorPorEstado(any())).thenReturn(BigDecimal.ZERO);
        when(quotaRepository.somaValorPorEstado(EstadoQuota.PAGO)).thenReturn(new BigDecimal("170.00"));
        when(despesaRepository.somaTotal()).thenReturn(new BigDecimal("430.50"));
        when(ocorrenciaRepository.countByEstado(any())).thenReturn(0L);
        when(ocorrenciaRepository.countByEstado(EstadoOcorrencia.CONCLUIDA)).thenReturn(1L);
        when(reuniaoRepository.countByEstado(EstadoReuniao.AGENDADA)).thenReturn(1L);

        DashboardResponse r = service.overview();

        assertThat(r.estrutura().utilizadores()).isEqualTo(2L);
        assertThat(r.financeiro().quotasContagem().get(EstadoQuota.PAGO)).isEqualTo(1L);
        assertThat(r.financeiro().quotasValor().get(EstadoQuota.PAGO)).isEqualByComparingTo("170.00");
        assertThat(r.financeiro().totalDespesas()).isEqualByComparingTo("430.50");
        assertThat(r.ocorrenciasPorEstado().get(EstadoOcorrencia.CONCLUIDA)).isEqualTo(1L);
        assertThat(r.reunioesAgendadas()).isEqualTo(1L);
    }
}
