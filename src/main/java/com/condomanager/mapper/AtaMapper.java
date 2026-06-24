package com.condomanager.mapper;

import com.condomanager.dto.AtaCreateDTO;
import com.condomanager.dto.AtaResponse;
import com.condomanager.dto.AtaUpdateDTO;
import com.condomanager.model.Ata;
import org.springframework.stereotype.Component;

/**
 * Conversão entre {@link Ata} e os respetivos DTOs.
 */
@Component
public class AtaMapper {

    public Ata toEntity(AtaCreateDTO dto) {
        Ata ata = new Ata();
        ata.setIdReuniao(dto.reuniaoId());
        ata.setTitulo(dto.titulo());
        ata.setDescricao(dto.descricao());
        ata.setDataReuniao(dto.dataReuniao());
        return ata;
    }

    public void aplicarAtualizacao(Ata ata, AtaUpdateDTO dto) {
        ata.setTitulo(dto.titulo());
        ata.setDescricao(dto.descricao());
        ata.setDataReuniao(dto.dataReuniao());
    }

    public AtaResponse toResponse(Ata ata) {
        return new AtaResponse(
                ata.getId(),
                ata.getIdReuniao(),
                ata.getTitulo(),
                ata.getDescricao(),
                ata.getDataReuniao(),
                ata.getFicheiro() != null,
                ata.getIdEmpresa(),
                ata.getCreatedAt(),
                ata.getUpdatedAt()
        );
    }
}
