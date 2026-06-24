package com.condomanager.mapper;

import com.condomanager.dto.VotacaoCreateDTO;
import com.condomanager.dto.VotacaoResponse;
import com.condomanager.dto.VotoResponse;
import com.condomanager.model.Reuniao;
import com.condomanager.model.Votacao;
import com.condomanager.model.Voto;
import org.springframework.stereotype.Component;

/**
 * Conversão de {@link Votacao}/{@link Voto} para as respetivas representações públicas.
 */
@Component
public class VotacaoMapper {

    public Votacao toEntity(VotacaoCreateDTO dto, Reuniao reuniao) {
        Votacao votacao = new Votacao();
        votacao.setReuniao(reuniao);
        votacao.setTema(dto.tema());
        votacao.setDataInicio(dto.dataInicio());
        votacao.setDataFim(dto.dataFim());
        votacao.setTipoMaioria(dto.tipoMaioria());
        return votacao;
    }

    public VotacaoResponse toResponse(Votacao votacao) {
        return new VotacaoResponse(
                votacao.getId(),
                votacao.getReuniao().getId(),
                votacao.getTema(),
                votacao.getDataInicio(),
                votacao.getDataFim(),
                votacao.getTipoMaioria(),
                votacao.getEstado(),
                votacao.getIdEmpresa(),
                votacao.getCreatedAt(),
                votacao.getUpdatedAt()
        );
    }

    public VotoResponse toVotoResponse(Voto voto) {
        return new VotoResponse(
                voto.getId(),
                voto.getVotacao().getId(),
                voto.getCondomino().getId(),
                voto.getResposta(),
                voto.getIdEmpresa(),
                voto.getCreatedAt()
        );
    }
}
