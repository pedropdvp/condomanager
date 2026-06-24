package com.condomanager.service;

import com.condomanager.dto.PagamentoCreateDTO;
import com.condomanager.mapper.PagamentoMapper;
import com.condomanager.model.EstadoPagamento;
import com.condomanager.model.EstadoQuota;
import com.condomanager.model.MetodoPagamento;
import com.condomanager.model.Pagamento;
import com.condomanager.model.Quota;
import com.condomanager.repository.PagamentoRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository repository;
    @Mock
    private QuotaRepository quotaRepository;

    private PagamentoService service;
    private final PagamentoMapper mapper = new PagamentoMapper();

    @BeforeEach
    void setUp() {
        service = new PagamentoService(repository, quotaRepository, mapper);
        TenantContext.setTenantId(2L);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    private Quota quota(EstadoQuota estado, String valor) {
        Quota q = new Quota();
        q.setId(1L);
        q.setIdEmpresa(2L);
        q.setValor(new BigDecimal(valor));
        q.setEstado(estado);
        return q;
    }

    @Test
    void pagamentoTotalMarcaQuotaComoPaga() {
        Quota q = quota(EstadoQuota.PENDENTE, "100.00");
        when(quotaRepository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(q));
        when(repository.save(any(Pagamento.class))).thenAnswer(inv -> {
            Pagamento p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        Pagamento confirmado = new Pagamento();
        confirmado.setValor(new BigDecimal("100.00"));
        confirmado.setEstado(EstadoPagamento.CONFIRMADO);
        when(repository.findByQuota_IdAndEstado(1L, EstadoPagamento.CONFIRMADO))
                .thenReturn(List.of(confirmado));

        service.registar(new PagamentoCreateDTO(1L, new BigDecimal("100.00"), MetodoPagamento.MBWAY, null));

        assertThat(q.getEstado()).isEqualTo(EstadoQuota.PAGO);
    }

    @Test
    void pagamentoParcialMantemPendente() {
        Quota q = quota(EstadoQuota.PENDENTE, "100.00");
        when(quotaRepository.findByIdAndIdEmpresa(1L, 2L)).thenReturn(Optional.of(q));
        when(repository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));
        Pagamento parcial = new Pagamento();
        parcial.setValor(new BigDecimal("40.00"));
        parcial.setEstado(EstadoPagamento.CONFIRMADO);
        when(repository.findByQuota_IdAndEstado(1L, EstadoPagamento.CONFIRMADO))
                .thenReturn(List.of(parcial));

        service.registar(new PagamentoCreateDTO(1L, new BigDecimal("40.00"), MetodoPagamento.DINHEIRO, null));

        assertThat(q.getEstado()).isEqualTo(EstadoQuota.PENDENTE);
    }

    @Test
    void naoPermitePagarQuotaAnulada() {
        when(quotaRepository.findByIdAndIdEmpresa(1L, 2L))
                .thenReturn(Optional.of(quota(EstadoQuota.ANULADO, "100.00")));

        assertThatThrownBy(() -> service.registar(
                new PagamentoCreateDTO(1L, new BigDecimal("100.00"), MetodoPagamento.TRANSFERENCIA, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
