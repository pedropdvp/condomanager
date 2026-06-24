package com.condomanager.mapper;

import com.condomanager.dto.CondominioCreateDTO;
import com.condomanager.dto.CondominioResponse;
import com.condomanager.dto.CondominioUpdateDTO;
import com.condomanager.model.Condominio;
import org.springframework.stereotype.Component;

/**
 * Conversão entre {@link Condominio} e os respetivos DTOs.
 */
@Component
public class CondominioMapper {

    public Condominio toEntity(CondominioCreateDTO dto) {
        Condominio condominio = new Condominio();
        condominio.setNome(dto.nome());
        condominio.setMorada(dto.morada());
        condominio.setOrcamentoAnual(dto.orcamentoAnual());
        return condominio;
    }

    public void aplicarAtualizacao(Condominio condominio, CondominioUpdateDTO dto) {
        condominio.setNome(dto.nome());
        condominio.setMorada(dto.morada());
        condominio.setOrcamentoAnual(dto.orcamentoAnual());
    }

    public CondominioResponse toResponse(Condominio condominio) {
        return new CondominioResponse(
                condominio.getId(),
                condominio.getNome(),
                condominio.getMorada(),
                condominio.getOrcamentoAnual(),
                condominio.getIdEmpresa(),
                condominio.getCreatedAt(),
                condominio.getUpdatedAt()
        );
    }
}
