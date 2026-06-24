package com.condomanager.mapper;

import com.condomanager.dto.PagamentoResponse;
import com.condomanager.model.Pagamento;
import org.springframework.stereotype.Component;

/**
 * Conversão de {@link Pagamento} para a sua representação pública.
 */
@Component
public class PagamentoMapper {

    public PagamentoResponse toResponse(Pagamento pagamento) {
        return new PagamentoResponse(
                pagamento.getId(),
                pagamento.getQuota().getId(),
                pagamento.getValor(),
                pagamento.getDataPagamento(),
                pagamento.getMetodo(),
                pagamento.getEstado(),
                pagamento.getIdEmpresa(),
                pagamento.getCreatedAt(),
                pagamento.getUpdatedAt()
        );
    }
}
