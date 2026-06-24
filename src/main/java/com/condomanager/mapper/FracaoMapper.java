package com.condomanager.mapper;

import com.condomanager.dto.FracaoCreateDTO;
import com.condomanager.dto.FracaoResponse;
import com.condomanager.dto.FracaoUpdateDTO;
import com.condomanager.model.Condominio;
import com.condomanager.model.Edificio;
import com.condomanager.model.Fracao;
import org.springframework.stereotype.Component;

/**
 * Conversão entre {@link Fracao} e os respetivos DTOs.
 */
@Component
public class FracaoMapper {

    public Fracao toEntity(FracaoCreateDTO dto, Condominio condominio, Edificio edificio) {
        Fracao fracao = new Fracao();
        fracao.setCondominio(condominio);
        fracao.setEdificio(edificio);
        fracao.setNumero(dto.numero());
        fracao.setTipologia(dto.tipologia());
        fracao.setPermilagem(dto.permilagem());
        fracao.setAreaM2(dto.areaM2());
        return fracao;
    }

    public void aplicarAtualizacao(Fracao fracao, FracaoUpdateDTO dto) {
        fracao.setNumero(dto.numero());
        fracao.setTipologia(dto.tipologia());
        fracao.setPermilagem(dto.permilagem());
        fracao.setAreaM2(dto.areaM2());
    }

    public FracaoResponse toResponse(Fracao fracao) {
        return new FracaoResponse(
                fracao.getId(),
                fracao.getCondominio().getId(),
                fracao.getEdificio().getId(),
                fracao.getNumero(),
                fracao.getTipologia(),
                fracao.getPermilagem(),
                fracao.getAreaM2(),
                fracao.getIdEmpresa(),
                fracao.getCreatedAt(),
                fracao.getUpdatedAt()
        );
    }
}
