package com.condomanager.service;

import com.condomanager.dto.ReuniaoCreateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.ReuniaoMapper;
import com.condomanager.model.Condominio;
import com.condomanager.model.EstadoReuniao;
import com.condomanager.model.Reuniao;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.ReuniaoRepository;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReuniaoServiceTest {

    @Mock
    private ReuniaoRepository repository;
    @Mock
    private CondominioRepository condominioRepository;
    @Mock
    private com.condomanager.repository.CondominoRepository condominoRepository;
    @Mock
    private EmailService emailService;

    private ReuniaoService service;
    private final ReuniaoMapper mapper = new ReuniaoMapper();

    @BeforeEach
    void setUp() {
        service = new ReuniaoService(repository, condominioRepository, condominoRepository, emailService, mapper);
        TenantContext.setTenantId(2L);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    @Test
    void criarComCondominioDeOutroTenantDeve404() {
        when(condominioRepository.findByIdAndIdEmpresa(99L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(
                new ReuniaoCreateDTO(99L, LocalDate.now(), LocalTime.of(18, 30), "Sala", "Ordem...")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void realizarDeAgendadaTransita() {
        Reuniao r = reuniaoAgendada();
        when(repository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(r));

        assertThat(service.marcarRealizada(1L).estado()).isEqualTo(EstadoReuniao.REALIZADA);
    }

    @Test
    void cancelarUmaReuniaoJaRealizadaFalha() {
        Reuniao r = reuniaoAgendada();
        r.setEstado(EstadoReuniao.REALIZADA);
        when(repository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> service.cancelar(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Reuniao reuniaoAgendada() {
        Condominio c = new Condominio();
        c.setId(7L);
        c.setIdEmpresa(2L);
        Reuniao r = new Reuniao();
        r.setId(1L);
        r.setIdEmpresa(2L);
        r.setCondominio(c);
        r.setData(LocalDate.now());
        r.setEstado(EstadoReuniao.AGENDADA);
        return r;
    }
}
