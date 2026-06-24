package com.condomanager.mapper;

import com.condomanager.dto.OcorrenciaResponse;
import com.condomanager.model.Ocorrencia;
import org.springframework.stereotype.Component;

/**
 * Conversão de {@link Ocorrencia} para a sua representação pública.
 */
@Component
public class OcorrenciaMapper {

    public OcorrenciaResponse toResponse(Ocorrencia o) {
        return new OcorrenciaResponse(
                o.getId(),
                o.getCondominio().getId(),
                o.getCondomino() != null ? o.getCondomino().getId() : null,
                o.getResponsavel() != null ? o.getResponsavel().getId() : null,
                o.getTitulo(),
                o.getDescricao(),
                o.getEstado(),
                o.getPrioridade(),
                o.getIdEmpresa(),
                o.getCreatedAt(),
                o.getUpdatedAt()
        );
    }
}
