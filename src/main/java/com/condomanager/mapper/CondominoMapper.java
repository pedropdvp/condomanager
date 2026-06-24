package com.condomanager.mapper;

import com.condomanager.dto.CondominoCreateDTO;
import com.condomanager.dto.CondominoResponse;
import com.condomanager.dto.CondominoUpdateDTO;
import com.condomanager.model.Condomino;
import com.condomanager.model.Fracao;
import org.springframework.stereotype.Component;

/**
 * Conversão entre {@link Condomino} e os respetivos DTOs.
 */
@Component
public class CondominoMapper {

    public Condomino toEntity(CondominoCreateDTO dto, Fracao fracao) {
        Condomino condomino = new Condomino();
        condomino.setFracao(fracao);
        condomino.setNome(dto.nome());
        condomino.setNif(dto.nif());
        condomino.setEmail(dto.email());
        condomino.setTelefone(dto.telefone());
        condomino.setTipo(dto.tipo());
        return condomino;
    }

    public void aplicarAtualizacao(Condomino condomino, CondominoUpdateDTO dto) {
        condomino.setNome(dto.nome());
        condomino.setNif(dto.nif());
        condomino.setEmail(dto.email());
        condomino.setTelefone(dto.telefone());
        condomino.setTipo(dto.tipo());
    }

    public CondominoResponse toResponse(Condomino condomino) {
        return new CondominoResponse(
                condomino.getId(),
                condomino.getFracao().getId(),
                condomino.getNome(),
                condomino.getNif(),
                condomino.getEmail(),
                condomino.getTelefone(),
                condomino.getTipo(),
                condomino.getIdEmpresa(),
                condomino.getCreatedAt(),
                condomino.getUpdatedAt()
        );
    }
}
