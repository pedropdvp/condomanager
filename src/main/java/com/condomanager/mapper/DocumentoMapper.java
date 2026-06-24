package com.condomanager.mapper;

import com.condomanager.dto.DocumentoResponse;
import com.condomanager.model.Documento;
import org.springframework.stereotype.Component;

/**
 * Conversão de {@link Documento} para a sua representação pública (metadados).
 */
@Component
public class DocumentoMapper {

    public DocumentoResponse toResponse(Documento documento) {
        return new DocumentoResponse(
                documento.getId(),
                documento.getCondominio().getId(),
                documento.getNome(),
                documento.getTipo(),
                documento.getIdEmpresa(),
                documento.getCreatedAt(),
                documento.getUpdatedAt()
        );
    }
}
