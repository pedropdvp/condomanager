package com.condomanager.mapper;

import com.condomanager.dto.QuotaResponse;
import com.condomanager.model.Quota;
import org.springframework.stereotype.Component;

/**
 * Conversão de {@link Quota} para a sua representação pública.
 */
@Component
public class QuotaMapper {

    public QuotaResponse toResponse(Quota quota) {
        return new QuotaResponse(
                quota.getId(),
                quota.getFracao().getId(),
                quota.getMes(),
                quota.getAno(),
                quota.getValor(),
                quota.getEstado(),
                quota.getIdEmpresa(),
                quota.getCreatedAt(),
                quota.getUpdatedAt()
        );
    }
}
