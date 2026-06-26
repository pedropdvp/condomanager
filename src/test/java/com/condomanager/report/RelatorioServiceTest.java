package com.condomanager.report;

import com.condomanager.dto.BalanceteResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Condominio;
import com.condomanager.model.EstadoQuota;
import com.condomanager.model.Fracao;
import com.condomanager.model.Quota;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.DespesaRepository;
import com.condomanager.repository.OcorrenciaRepository;
import com.condomanager.repository.QuotaRepository;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RelatorioServiceTest {

    @Mock private CondominioRepository condominioRepository;
    @Mock private QuotaRepository quotaRepository;
    @Mock private DespesaRepository despesaRepository;
    @Mock private OcorrenciaRepository ocorrenciaRepository;

    private RelatorioService service;

    @BeforeEach
    void setUp() {
        service = new RelatorioService(condominioRepository, quotaRepository, despesaRepository, ocorrenciaRepository);
        TenantContext.setTenantId(2L);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    private Quota quota() {
        Fracao f = new Fracao();
        f.setNumero("1A");
        Quota q = new Quota();
        q.setFracao(f);
        q.setMes(6);
        q.setAno(2026);
        q.setValor(new BigDecimal("170.00"));
        q.setEstado(EstadoQuota.PAGO);
        return q;
    }

    @Test
    void condominioDeOutroTenantDeve404() {
        when(condominioRepository.findByIdAndIdEmpresa(99L, 2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.relatorioQuotasPdf(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void geraPdfValido() {
        Condominio c = new Condominio();
        c.setId(1L);
        c.setIdEmpresa(2L);
        c.setNome("Residencial Alfa");
        when(condominioRepository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(c));
        when(quotaRepository.findByFracao_Condominio_Id(1L)).thenReturn(List.of(quota()));

        byte[] pdf = service.relatorioQuotasPdf(1L);

        assertThat(pdf).isNotEmpty();
        // Assinatura de um ficheiro PDF: "%PDF"
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void geraPdfDespesasComTemplateGenerico() {
        Condominio c = new Condominio();
        c.setId(1L);
        c.setIdEmpresa(2L);
        c.setNome("Residencial Alfa");
        when(condominioRepository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(c));
        when(despesaRepository.findByCondominio_Id(eq(1L), any())).thenReturn(Page.empty());

        byte[] pdf = service.relatorioDespesasPdf(1L);

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void balanceteCalculaSaldoEFundoReserva() {
        Condominio c = new Condominio();
        c.setId(1L);
        c.setIdEmpresa(2L);
        c.setNome("Residencial Alfa");
        c.setOrcamentoAnual(new BigDecimal("1200.00"));
        when(condominioRepository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(c));
        when(quotaRepository.findByFracao_Condominio_Id(1L)).thenReturn(List.of(quota())); // PAGO 170.00
        when(despesaRepository.findByCondominio_Id(eq(1L), any())).thenReturn(Page.empty());

        BalanceteResponse b = service.balancete(1L);

        assertThat(b.quotasPagas()).isEqualByComparingTo("170.00");
        assertThat(b.fundoReserva()).isEqualByComparingTo("17.00"); // 10% de 170
        assertThat(b.saldo()).isEqualByComparingTo("170.00");
    }
}
