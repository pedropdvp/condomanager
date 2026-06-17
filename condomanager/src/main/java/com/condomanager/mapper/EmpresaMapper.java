package com.condomanager.mapper;

import com.condomanager.dto.EmpresaDTO;
import com.condomanager.model.EmpresaGestao;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {

    public EmpresaDTO toDTO(EmpresaGestao e) {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(e.getId());
        dto.setNome(e.getNome());
        dto.setNif(e.getNif());
        dto.setEmail(e.getEmail());
        dto.setTelefone(e.getTelefone());
        dto.setMorada(e.getMorada());
        dto.setEstado(e.getEstado());
        dto.setPlano(e.getPlano());
        return dto;
    }

    public EmpresaGestao toEntity(EmpresaDTO dto) {
        EmpresaGestao e = new EmpresaGestao();
        aplicar(dto, e);
        return e;
    }

    public void aplicar(EmpresaDTO dto, EmpresaGestao e) {
        e.setNome(dto.getNome());
        e.setNif(dto.getNif());
        e.setEmail(dto.getEmail());
        e.setTelefone(dto.getTelefone());
        e.setMorada(dto.getMorada());
        if (dto.getEstado() != null) {
            e.setEstado(dto.getEstado());
        }
        if (dto.getPlano() != null) {
            e.setPlano(dto.getPlano());
        }
    }
}
