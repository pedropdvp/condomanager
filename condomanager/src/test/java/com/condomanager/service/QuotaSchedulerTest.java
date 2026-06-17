package com.condomanager.service;

import com.condomanager.model.Condominio;
import com.condomanager.model.Fracao;
import com.condomanager.model.Quota;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.FracaoRepository;
import com.condomanager.repository.QuotaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuotaSchedulerTest {

    @Mock CondominioRepository condominioRepository;
    @Mock FracaoRepository fracaoRepository;
    @Mock QuotaRepository quotaRepository;

    @InjectMocks QuotaScheduler scheduler;

    @Test
    void calculaQuotaPorPermilagem() {
        // orcamento 24000/ano => 2000/mes; permilagem 500/1000 => 1000 EUR
        Condominio c = new Condominio();
        c.setId(1L);
        c.setOrcamentoAnual(new BigDecimal("24000"));

        Fracao f = new Fracao();
        f.setId(1L);
        f.setPermilagem(new BigDecimal("500"));
        f.setCondominio(c);

        when(condominioRepository.findAll()).thenReturn(List.of(c));
        when(fracaoRepository.findByCondominioId(1L)).thenReturn(List.of(f));
        when(quotaRepository.existsByFracaoIdAndMesAndAno(eq(1L), anyInt(), anyInt())).thenReturn(false);

        scheduler.gerarQuotasMensais();

        ArgumentCaptor<Quota> captor = ArgumentCaptor.forClass(Quota.class);
        org.mockito.Mockito.verify(quotaRepository).save(captor.capture());
        assertThat(captor.getValue().getValor()).isEqualByComparingTo("1000.00");
    }

    @Test
    void naoDuplicaQuotaExistente() {
        Condominio c = new Condominio();
        c.setId(1L);
        c.setOrcamentoAnual(new BigDecimal("12000"));
        Fracao f = new Fracao();
        f.setId(1L);
        f.setPermilagem(new BigDecimal("1000"));
        f.setCondominio(c);

        when(condominioRepository.findAll()).thenReturn(List.of(c));
        when(fracaoRepository.findByCondominioId(1L)).thenReturn(List.of(f));
        when(quotaRepository.existsByFracaoIdAndMesAndAno(eq(1L), anyInt(), anyInt())).thenReturn(true);

        scheduler.gerarQuotasMensais();

        org.mockito.Mockito.verify(quotaRepository, org.mockito.Mockito.never()).save(any());
    }
}
