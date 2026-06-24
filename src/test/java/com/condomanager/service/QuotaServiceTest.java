package com.condomanager.service;

import com.condomanager.dto.GeracaoQuotasResultado;
import com.condomanager.dto.GerarQuotasDTO;
import com.condomanager.mapper.QuotaMapper;
import com.condomanager.model.Condominio;
import com.condomanager.model.Fracao;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.FracaoRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuotaServiceTest {

    @Mock
    private QuotaRepository repository;
    @Mock
    private CondominioRepository condominioRepository;
    @Mock
    private FracaoRepository fracaoRepository;

    private QuotaService service;
    private final QuotaMapper mapper = new QuotaMapper();

    @BeforeEach
    void setUp() {
        service = new QuotaService(repository, condominioRepository, fracaoRepository, mapper);
        TenantContext.setTenantId(2L);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    private Fracao fracao(Long id, String permilagem) {
        Fracao f = new Fracao();
        f.setId(id);
        f.setIdEmpresa(2L);
        f.setPermilagem(new BigDecimal(permilagem));
        return f;
    }

    @Test
    void gerarCalculaValorPorPermilagem() {
        Condominio cond = new Condominio();
        cond.setId(1L);
        cond.setIdEmpresa(2L);
        cond.setOrcamentoAnual(new BigDecimal("120000.00"));
        when(condominioRepository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(cond));
        // permilagens 85 + 915 = 1000 ‰  -> total mensal = orçamento/12 = 10000.00
        when(fracaoRepository.findByCondominio_Id(1L))
                .thenReturn(List.of(fracao(10L, "85.0000"), fracao(11L, "915.0000")));
        when(repository.existsByFracao_IdAndMesAndAno(any(), anyInt(), anyInt())).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        GeracaoQuotasResultado r = service.gerar(new GerarQuotasDTO(1L, 6, 2026));

        assertThat(r.quotasGeradas()).isEqualTo(2);
        assertThat(r.fracoesIgnoradas()).isZero();
        // 120000 * 85 / 12000 = 850.00 ; 120000 * 915 / 12000 = 9150.00
        assertThat(r.valorTotal()).isEqualByComparingTo("10000.00");
    }

    @Test
    void gerarIgnoraFracoesComQuotaExistente() {
        Condominio cond = new Condominio();
        cond.setId(1L);
        cond.setIdEmpresa(2L);
        cond.setOrcamentoAnual(new BigDecimal("120000.00"));
        when(condominioRepository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(cond));
        when(fracaoRepository.findByCondominio_Id(1L)).thenReturn(List.of(fracao(10L, "100.0000")));
        when(repository.existsByFracao_IdAndMesAndAno(eq(10L), eq(6), eq(2026))).thenReturn(true);

        GeracaoQuotasResultado r = service.gerar(new GerarQuotasDTO(1L, 6, 2026));

        assertThat(r.quotasGeradas()).isZero();
        assertThat(r.fracoesIgnoradas()).isEqualTo(1);
        assertThat(r.valorTotal()).isEqualByComparingTo("0");
    }
}
