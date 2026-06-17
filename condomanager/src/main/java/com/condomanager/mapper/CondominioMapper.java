package com.condomanager.mapper;

import com.condomanager.dto.CondominioDTO;
import com.condomanager.model.Condominio;
import org.springframework.stereotype.Component;

@Component
public class CondominioMapper {

    public CondominioDTO toDTO(Condominio c) {
        CondominioDTO dto = new CondominioDTO();
        dto.setId(c.getId());
        dto.setNome(c.getNome());
        dto.setMorada(c.getMorada());
        dto.setOrcamentoAnual(c.getOrcamentoAnual());
        dto.setEmpresaId(c.getEmpresa() != null ? c.getEmpresa().getId() : null);
        return dto;
    }
}
