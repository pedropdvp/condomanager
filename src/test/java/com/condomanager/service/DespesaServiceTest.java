package com.condomanager.service;

import com.condomanager.dto.DespesaCreateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.DespesaMapper;
import com.condomanager.model.CategoriaDespesa;
import com.condomanager.model.Condominio;
import com.condomanager.model.Despesa;
import com.condomanager.model.EstadoDespesa;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.DespesaRepository;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DespesaServiceTest {

    @Mock
    private DespesaRepository repository;
    @Mock
    private CondominioRepository condominioRepository;

    private DespesaService service;
    private final DespesaMapper mapper = new DespesaMapper();

    @BeforeEach
    void setUp() {
        service = new DespesaService(repository, condominioRepository, mapper);
        TenantContext.setTenantId(2L);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    @Test
    void criarComCondominioDeOutroTenantDeve404() {
        when(condominioRepository.findByIdAndIdEmpresa(99L, 2L)).thenReturn(Optional.empty());

        DespesaCreateDTO dto = new DespesaCreateDTO(
                99L, "Limpeza escadas", CategoriaDespesa.LIMPEZA, new BigDecimal("120.00"), LocalDate.now());

        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void criarValidoDevePersistir() {
        Condominio cond = new Condominio();
        cond.setId(7L);
        cond.setIdEmpresa(2L);
        when(condominioRepository.findByIdAndIdEmpresa(7L, 2L)).thenReturn(Optional.of(cond));
        when(repository.save(any(Despesa.class))).thenAnswer(inv -> {
            Despesa d = inv.getArgument(0);
            d.setId(1L);
            d.setIdEmpresa(2L);
            return d;
        });

        var r = service.criar(new DespesaCreateDTO(
                7L, "Manutenção elevador", CategoriaDespesa.MANUTENCAO, new BigDecimal("250.00"), LocalDate.now()));

        assertThat(r.id()).isEqualTo(1L);
        assertThat(r.condominioId()).isEqualTo(7L);
        assertThat(r.categoria()).isEqualTo(CategoriaDespesa.MANUTENCAO);
        assertThat(r.estado()).isEqualTo(EstadoDespesa.PENDENTE);
    }

    @Test
    void aprovarDespesaPendenteMudaParaAprovada() {
        Despesa d = new Despesa();
        d.setId(1L);
        d.setIdEmpresa(2L);
        d.setCondominio(condominio());
        d.setEstado(EstadoDespesa.PENDENTE);
        when(repository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(d));

        assertThat(service.aprovar(1L).estado()).isEqualTo(EstadoDespesa.APROVADA);
    }

    @Test
    void aprovarDespesaJaDecididaFalha() {
        Despesa d = new Despesa();
        d.setId(1L);
        d.setIdEmpresa(2L);
        d.setCondominio(condominio());
        d.setEstado(EstadoDespesa.APROVADA);
        when(repository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(d));

        assertThatThrownBy(() -> service.rejeitar(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Condominio condominio() {
        Condominio c = new Condominio();
        c.setId(7L);
        c.setIdEmpresa(2L);
        return c;
    }
}
