package com.condomanager.mapper;

import com.condomanager.dto.EmpresaCreateDTO;
import com.condomanager.dto.EmpresaResponse;
import com.condomanager.dto.EmpresaUpdateDTO;
import com.condomanager.model.EmpresaGestao;
import com.condomanager.model.EstadoEmpresa;
import org.springframework.stereotype.Component;

/**
 * Conversão entre {@link EmpresaGestao} e os respetivos DTOs.
 */
@Component
public class EmpresaMapper {

    public EmpresaGestao toEntity(EmpresaCreateDTO dto) {
        EmpresaGestao empresa = new EmpresaGestao();
        empresa.setNome(dto.nome());
        empresa.setNif(dto.nif());
        empresa.setEmail(dto.email());
        empresa.setTelefone(dto.telefone());
        empresa.setMorada(dto.morada());
        empresa.setEstado(EstadoEmpresa.ATIVO);
        return empresa;
    }

    public void aplicarAtualizacao(EmpresaGestao empresa, EmpresaUpdateDTO dto) {
        empresa.setNome(dto.nome());
        empresa.setEmail(dto.email());
        empresa.setTelefone(dto.telefone());
        empresa.setMorada(dto.morada());
        empresa.setEstado(dto.estado());
    }

    public EmpresaResponse toResponse(EmpresaGestao empresa) {
        return new EmpresaResponse(
                empresa.getId(),
                empresa.getNome(),
                empresa.getNif(),
                empresa.getEmail(),
                empresa.getTelefone(),
                empresa.getMorada(),
                empresa.getEstado(),
                empresa.getCreatedAt(),
                empresa.getUpdatedAt()
        );
    }
}
