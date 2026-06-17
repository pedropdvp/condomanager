package com.condomanager.service;

import com.condomanager.dto.PagamentoDTO;
import com.condomanager.model.Pagamento;
import com.condomanager.model.Quota;
import com.condomanager.model.enums.EstadoQuota;
import com.condomanager.model.enums.MetodoPagamento;
import com.condomanager.repository.PagamentoRepository;
import com.condomanager.repository.QuotaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock PagamentoRepository pagamentoRepository;
    @Mock QuotaRepository quotaRepository;

    @InjectMocks PagamentoService service;

    @Test
    void registarPagamentoMarcaQuotaComoPaga() {
        Quota quota = new Quota();
        quota.setId(3L);
        quota.setEstado(EstadoQuota.PENDENTE);
        quota.setValor(new BigDecimal("100"));

        when(quotaRepository.findById(3L)).thenReturn(Optional.of(quota));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> {
            Pagamento p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PagamentoDTO dto = new PagamentoDTO();
        dto.setValor(new BigDecimal("100"));
        dto.setMetodo(MetodoPagamento.MBWAY);
        dto.setQuotaId(3L);

        PagamentoDTO resultado = service.registar(dto);

        assertThat(resultado.getId()).isEqualTo(1L);

        ArgumentCaptor<Quota> captor = ArgumentCaptor.forClass(Quota.class);
        org.mockito.Mockito.verify(quotaRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoQuota.PAGA);
    }
}
