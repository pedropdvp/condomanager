package com.condomanager.mapper;

import com.condomanager.dto.MensagemResponse;
import com.condomanager.model.Mensagem;
import org.springframework.stereotype.Component;

/**
 * Conversão de {@link Mensagem} para a sua representação pública.
 */
@Component
public class MensagemMapper {

    public MensagemResponse toResponse(Mensagem mensagem) {
        return new MensagemResponse(
                mensagem.getId(),
                mensagem.getTipo(),
                mensagem.getAssunto(),
                mensagem.getConteudo(),
                mensagem.getDataEnvio(),
                mensagem.getOrigem().getId(),
                mensagem.getOrigem().getNome(),
                mensagem.getDestino() != null ? mensagem.getDestino().getId() : null,
                mensagem.isLida(),
                mensagem.getIdEmpresa(),
                mensagem.getCreatedAt()
        );
    }
}
