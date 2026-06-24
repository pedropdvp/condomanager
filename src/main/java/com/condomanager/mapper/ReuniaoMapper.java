package com.condomanager.mapper;

import com.condomanager.dto.ConvocatoriaResponse;
import com.condomanager.dto.ReuniaoCreateDTO;
import com.condomanager.dto.ReuniaoResponse;
import com.condomanager.dto.ReuniaoUpdateDTO;
import com.condomanager.model.Condominio;
import com.condomanager.model.Reuniao;
import org.springframework.stereotype.Component;

/**
 * Conversão entre {@link Reuniao} e os respetivos DTOs.
 */
@Component
public class ReuniaoMapper {

    private static final int ANTECEDENCIA_MINIMA_DIAS = 10;
    private static final String OBSERVACAO_LEGAL =
            "A convocatória deve ser enviada com pelo menos 10 dias de antecedência, "
                    + "por carta registada ou correio eletrónico (Lei 8/2022).";

    public Reuniao toEntity(ReuniaoCreateDTO dto, Condominio condominio) {
        Reuniao reuniao = new Reuniao();
        reuniao.setCondominio(condominio);
        reuniao.setData(dto.data());
        reuniao.setHora(dto.hora());
        reuniao.setLocal(dto.local());
        reuniao.setOrdemTrabalhos(dto.ordemTrabalhos());
        return reuniao;
    }

    public void aplicarAtualizacao(Reuniao reuniao, ReuniaoUpdateDTO dto) {
        reuniao.setData(dto.data());
        reuniao.setHora(dto.hora());
        reuniao.setLocal(dto.local());
        reuniao.setOrdemTrabalhos(dto.ordemTrabalhos());
    }

    public ReuniaoResponse toResponse(Reuniao reuniao) {
        return new ReuniaoResponse(
                reuniao.getId(),
                reuniao.getCondominio().getId(),
                reuniao.getData(),
                reuniao.getHora(),
                reuniao.getLocal(),
                reuniao.getOrdemTrabalhos(),
                reuniao.getEstado(),
                reuniao.getIdEmpresa(),
                reuniao.getCreatedAt(),
                reuniao.getUpdatedAt()
        );
    }

    public ConvocatoriaResponse toConvocatoria(Reuniao reuniao) {
        return new ConvocatoriaResponse(
                reuniao.getId(),
                reuniao.getCondominio().getId(),
                reuniao.getData(),
                reuniao.getHora(),
                reuniao.getLocal(),
                reuniao.getOrdemTrabalhos(),
                ANTECEDENCIA_MINIMA_DIAS,
                OBSERVACAO_LEGAL
        );
    }
}
