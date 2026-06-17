package com.condomanager.service;

import com.condomanager.dto.ResultadoVotacaoDTO;
import com.condomanager.dto.VotoDTO;
import com.condomanager.model.Condomino;
import com.condomanager.model.Votacao;
import com.condomanager.model.enums.EstadoVotacao;
import com.condomanager.model.enums.RespostaVoto;
import com.condomanager.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotacaoServiceTest {

    @Mock VotacaoRepository votacaoRepository;
    @Mock VotoRepository votoRepository;
    @Mock CondominioRepository condominioRepository;
    @Mock ReuniaoRepository reuniaoRepository;
    @Mock CondominoRepository condominoRepository;

    @InjectMocks VotacaoService service;

    private Votacao votacaoAberta() {
        Votacao v = new Votacao();
        v.setId(5L);
        v.setTema("Tema");
        v.setEstado(EstadoVotacao.ABERTA);
        return v;
    }

    private VotoDTO voto() {
        VotoDTO d = new VotoDTO();
        d.setVotacaoId(5L);
        d.setCondominoId(7L);
        d.setResposta(RespostaVoto.SIM);
        return d;
    }

    @Test
    void registaVotoEContaResultado() {
        when(votacaoRepository.findById(5L)).thenReturn(Optional.of(votacaoAberta()));
        when(votoRepository.existsByVotacaoIdAndCondominoId(5L, 7L)).thenReturn(false);
        when(condominoRepository.findById(7L)).thenReturn(Optional.of(new Condomino()));
        when(votoRepository.countByVotacaoIdAndResposta(5L, RespostaVoto.SIM)).thenReturn(1L);
        when(votoRepository.countByVotacaoIdAndResposta(5L, RespostaVoto.NAO)).thenReturn(0L);
        when(votoRepository.countByVotacaoIdAndResposta(5L, RespostaVoto.ABSTENCAO)).thenReturn(0L);

        ResultadoVotacaoDTO r = service.votar(voto());

        assertThat(r.getSim()).isEqualTo(1);
        assertThat(r.getTotal()).isEqualTo(1);
        verify(votoRepository).save(any());
    }

    @Test
    void recusaVotoDuplicado() {
        when(votacaoRepository.findById(5L)).thenReturn(Optional.of(votacaoAberta()));
        when(votoRepository.existsByVotacaoIdAndCondominoId(5L, 7L)).thenReturn(true);

        assertThatThrownBy(() -> service.votar(voto()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ja votou");

        verify(votoRepository, never()).save(any());
    }

    @Test
    void recusaVotoEmVotacaoEncerrada() {
        Votacao encerrada = votacaoAberta();
        encerrada.setEstado(EstadoVotacao.ENCERRADA);
        when(votacaoRepository.findById(5L)).thenReturn(Optional.of(encerrada));

        assertThatThrownBy(() -> service.votar(voto()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("encerrada");

        verify(votoRepository, never()).save(any());
    }
}
