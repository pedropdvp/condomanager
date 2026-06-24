package com.condomanager.mapper;

import com.condomanager.dto.EdificioCreateDTO;
import com.condomanager.dto.EdificioResponse;
import com.condomanager.dto.EdificioUpdateDTO;
import com.condomanager.model.Condominio;
import com.condomanager.model.Edificio;
import org.springframework.stereotype.Component;

/**
 * Conversão entre {@link Edificio} e os respetivos DTOs.
 */
@Component
public class EdificioMapper {

    public Edificio toEntity(EdificioCreateDTO dto, Condominio condominio) {
        Edificio edificio = new Edificio();
        edificio.setCondominio(condominio);
        edificio.setNome(dto.nome());
        edificio.setBloco(dto.bloco());
        edificio.setNumeroPisos(dto.numeroPisos());
        return edificio;
    }

    public void aplicarAtualizacao(Edificio edificio, EdificioUpdateDTO dto) {
        edificio.setNome(dto.nome());
        edificio.setBloco(dto.bloco());
        edificio.setNumeroPisos(dto.numeroPisos());
    }

    public EdificioResponse toResponse(Edificio edificio) {
        return new EdificioResponse(
                edificio.getId(),
                edificio.getCondominio().getId(),
                edificio.getNome(),
                edificio.getBloco(),
                edificio.getNumeroPisos(),
                edificio.getIdEmpresa(),
                edificio.getCreatedAt(),
                edificio.getUpdatedAt()
        );
    }
}
