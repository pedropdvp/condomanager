package com.condomanager.service;

import com.condomanager.dto.FracaoDTO;
import com.condomanager.model.Condominio;
import com.condomanager.model.EmpresaGestao;
import com.condomanager.model.Fracao;
import com.condomanager.model.enums.Plano;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.EdificioRepository;
import com.condomanager.repository.FracaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FracaoServiceTest {

    @Mock FracaoRepository fracaoRepository;
    @Mock CondominioRepository condominioRepository;
    @Mock EdificioRepository edificioRepository;

    @InjectMocks FracaoService service;

    private Condominio condominio;

    @BeforeEach
    void setUp() {
        EmpresaGestao empresa = new EmpresaGestao("Gestao", "500000000", "g@x.pt");
        empresa.setId(1L);
        empresa.setPlano(Plano.STARTER);
        condominio = new Condominio();
        condominio.setId(10L);
        condominio.setEmpresa(empresa);
    }

    private FracaoDTO dto(BigDecimal permilagem) {
        FracaoDTO d = new FracaoDTO();
        d.setNumero("A1");
        d.setPermilagem(permilagem);
        d.setCondominioId(10L);
        return d;
    }

    @Test
    void criaFracaoQuandoDentroDosLimites() {
        when(condominioRepository.findById(10L)).thenReturn(Optional.of(condominio));
        when(fracaoRepository.countByCondominioEmpresaId(1L)).thenReturn(0L);
        when(fracaoRepository.findByCondominioId(10L)).thenReturn(List.of());
        when(fracaoRepository.save(any(Fracao.class))).thenAnswer(inv -> {
            Fracao f = inv.getArgument(0);
            f.setId(99L);
            return f;
        });

        FracaoDTO resultado = service.criar(dto(new BigDecimal("500")));

        assertThat(resultado.getId()).isEqualTo(99L);
        verify(fracaoRepository).save(any(Fracao.class));
    }

    @Test
    void recusaQuandoLimiteDoPlanoAtingido() {
        when(condominioRepository.findById(10L)).thenReturn(Optional.of(condominio));
        when(fracaoRepository.countByCondominioEmpresaId(1L))
                .thenReturn((long) Plano.STARTER.getLimiteFracoes()); // 50

        assertThatThrownBy(() -> service.criar(dto(new BigDecimal("10"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Limite do plano");

        verify(fracaoRepository, never()).save(any());
    }

    @Test
    void recusaQuandoPermilagemExcede1000() {
        Fracao existente = new Fracao();
        existente.setId(1L);
        existente.setPermilagem(new BigDecimal("600"));

        when(condominioRepository.findById(10L)).thenReturn(Optional.of(condominio));
        when(fracaoRepository.countByCondominioEmpresaId(1L)).thenReturn(1L);
        when(fracaoRepository.findByCondominioId(10L)).thenReturn(List.of(existente));

        assertThatThrownBy(() -> service.criar(dto(new BigDecimal("500")))) // 600 + 500 = 1100
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("permilagens");

        verify(fracaoRepository, never()).save(any());
    }
}
