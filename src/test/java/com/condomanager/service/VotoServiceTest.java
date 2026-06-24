package com.condomanager.service;

import com.condomanager.dto.VotoCreateDTO;
import com.condomanager.mapper.VotacaoMapper;
import com.condomanager.model.Condomino;
import com.condomanager.model.Condominio;
import com.condomanager.model.EstadoVotacao;
import com.condomanager.model.Fracao;
import com.condomanager.model.Reuniao;
import com.condomanager.model.RespostaVoto;
import com.condomanager.model.TipoCondomino;
import com.condomanager.model.Votacao;
import com.condomanager.repository.CondominoRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VotoServiceTest {

    @Mock private VotoRepository repository;
    @Mock private VotacaoRepository votacaoRepository;
    @Mock private CondominoRepository condominoRepository;
    @Mock private com.condomanager.repository.UtilizadorRepository utilizadorRepository;

    private VotoService service;
    private final VotacaoMapper mapper = new VotacaoMapper();

    @BeforeEach
    void setUp() {
        service = new VotoService(repository, votacaoRepository, condominoRepository, utilizadorRepository, mapper);
        TenantContext.setTenantId(2L);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    private Votacao votacao(EstadoVotacao estado, Long condominioId) {
        Condominio cond = new Condominio();
        cond.setId(condominioId);
        Reuniao r = new Reuniao();
        r.setCondominio(cond);
        Votacao v = new Votacao();
        v.setId(1L);
        v.setIdEmpresa(2L);
        v.setReuniao(r);
        v.setEstado(estado);
        return v;
    }

    private Condomino condomino(TipoCondomino tipo, Long condominioId) {
        Condominio cond = new Condominio();
        cond.setId(condominioId);
        Fracao f = new Fracao();
        f.setCondominio(cond);
        Condomino c = new Condomino();
        c.setId(5L);
        c.setTipo(tipo);
        c.setFracao(f);
        return c;
    }

    @Test
    void naoPermiteVotarSeVotacaoNaoAberta() {
        when(votacaoRepository.findByIdAndIdEmpresa(1L, 2L))
                .thenReturn(Optional.of(votacao(EstadoVotacao.CRIADA, 7L)));

        assertThatThrownBy(() -> service.votar(1L, new VotoCreateDTO(5L, RespostaVoto.SIM)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não está aberta");
    }

    @Test
    void inquilinoNaoPodeVotar() {
        when(votacaoRepository.findByIdAndIdEmpresa(1L, 2L))
                .thenReturn(Optional.of(votacao(EstadoVotacao.ABERTA, 7L)));
        when(condominoRepository.findByIdAndIdEmpresa(5L, 2L))
                .thenReturn(Optional.of(condomino(TipoCondomino.INQUILINO, 7L)));

        assertThatThrownBy(() -> service.votar(1L, new VotoCreateDTO(5L, RespostaVoto.SIM)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("proprietários");
    }

    @Test
    void condominoDeOutroCondominioNaoPodeVotar() {
        when(votacaoRepository.findByIdAndIdEmpresa(1L, 2L))
                .thenReturn(Optional.of(votacao(EstadoVotacao.ABERTA, 7L)));
        when(condominoRepository.findByIdAndIdEmpresa(5L, 2L))
                .thenReturn(Optional.of(condomino(TipoCondomino.PROPRIETARIO, 99L)));

        assertThatThrownBy(() -> service.votar(1L, new VotoCreateDTO(5L, RespostaVoto.SIM)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pertence ao condomínio");
    }
}
