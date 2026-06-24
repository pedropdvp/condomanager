package com.condomanager.service;

import com.condomanager.dto.ResultadoVotacaoResponse;
import com.condomanager.mapper.VotacaoMapper;
import com.condomanager.model.Condomino;
import com.condomanager.model.Condominio;
import com.condomanager.model.EstadoVotacao;
import com.condomanager.model.Fracao;
import com.condomanager.model.Reuniao;
import com.condomanager.model.RespostaVoto;
import com.condomanager.model.TipoMaioria;
import com.condomanager.model.Votacao;
import com.condomanager.model.Voto;
import com.condomanager.repository.FracaoRepository;
import com.condomanager.repository.ReuniaoRepository;
import com.condomanager.repository.VotacaoRepository;
import com.condomanager.repository.VotoRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VotacaoServiceTest {

    @Mock private VotacaoRepository repository;
    @Mock private ReuniaoRepository reuniaoRepository;
    @Mock private VotoRepository votoRepository;
    @Mock private FracaoRepository fracaoRepository;

    private VotacaoService service;
    private final VotacaoMapper mapper = new VotacaoMapper();

    @BeforeEach
    void setUp() {
        service = new VotacaoService(repository, reuniaoRepository, votoRepository, fracaoRepository, mapper);
        TenantContext.setTenantId(2L);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    private void cenario(TipoMaioria tipo, List<Voto> votos) {
        Condominio cond = new Condominio();
        cond.setId(7L);
        Reuniao reuniao = new Reuniao();
        reuniao.setCondominio(cond);
        Votacao v = new Votacao();
        v.setId(1L);
        v.setIdEmpresa(2L);
        v.setTema("Tema");
        v.setReuniao(reuniao);
        v.setTipoMaioria(tipo);
        v.setEstado(EstadoVotacao.ENCERRADA);
        when(repository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(v));
        // capital total = 1000 ‰
        Fracao total = new Fracao();
        total.setPermilagem(new BigDecimal("1000"));
        when(fracaoRepository.findByCondominio_Id(7L)).thenReturn(List.of(total));
        when(votoRepository.findByVotacao_Id(1L)).thenReturn(votos);
    }

    private Voto voto(RespostaVoto resposta, String permilagem) {
        Fracao f = new Fracao();
        f.setPermilagem(new BigDecimal(permilagem));
        Condomino c = new Condomino();
        c.setFracao(f);
        Voto v = new Voto();
        v.setResposta(resposta);
        v.setCondomino(c);
        return v;
    }

    @Test
    void maioriaSimplesAprovaComMaisDe50PorCentoDoPresente() {
        cenario(TipoMaioria.MAIORIA_SIMPLES,
                List.of(voto(RespostaVoto.SIM, "600"), voto(RespostaVoto.NAO, "400")));
        ResultadoVotacaoResponse r = service.resultado(1L);
        assertThat(r.somaSim()).isEqualByComparingTo("600");
        assertThat(r.capitalPresente()).isEqualByComparingTo("1000");
        assertThat(r.aprovado()).isTrue();
    }

    @Test
    void maioriaSimplesNaoAprovaSemMaioria() {
        cenario(TipoMaioria.MAIORIA_SIMPLES,
                List.of(voto(RespostaVoto.SIM, "400"), voto(RespostaVoto.NAO, "600")));
        assertThat(service.resultado(1L).aprovado()).isFalse();
    }

    @Test
    void doisTercosExigeDoisTercosDoCapitalTotal() {
        cenario(TipoMaioria.DOIS_TERCOS, List.of(voto(RespostaVoto.SIM, "700")));
        assertThat(service.resultado(1L).aprovado()).isTrue();

        cenario(TipoMaioria.DOIS_TERCOS, List.of(voto(RespostaVoto.SIM, "600")));
        assertThat(service.resultado(1L).aprovado()).isFalse();
    }

    @Test
    void semOposicaoExigeZeroVotosContra() {
        cenario(TipoMaioria.SEM_OPOSICAO,
                List.of(voto(RespostaVoto.SIM, "600"), voto(RespostaVoto.ABSTENCAO, "100")));
        assertThat(service.resultado(1L).aprovado()).isTrue();

        cenario(TipoMaioria.SEM_OPOSICAO,
                List.of(voto(RespostaVoto.SIM, "600"), voto(RespostaVoto.NAO, "10")));
        assertThat(service.resultado(1L).aprovado()).isFalse();
    }

    @Test
    void unanimidadeExigeCemPorCento() {
        cenario(TipoMaioria.UNANIMIDADE, List.of(voto(RespostaVoto.SIM, "1000")));
        assertThat(service.resultado(1L).aprovado()).isTrue();

        cenario(TipoMaioria.UNANIMIDADE, List.of(voto(RespostaVoto.SIM, "900")));
        assertThat(service.resultado(1L).aprovado()).isFalse();
    }
}
