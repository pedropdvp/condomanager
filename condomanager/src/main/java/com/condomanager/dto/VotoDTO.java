package com.condomanager.dto;

import com.condomanager.model.enums.RespostaVoto;
import jakarta.validation.constraints.NotNull;

public class VotoDTO {

    private Long id;

    @NotNull
    private RespostaVoto resposta;

    @NotNull
    private Long votacaoId;

    @NotNull
    private Long condominoId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RespostaVoto getResposta() { return resposta; }
    public void setResposta(RespostaVoto resposta) { this.resposta = resposta; }

    public Long getVotacaoId() { return votacaoId; }
    public void setVotacaoId(Long votacaoId) { this.votacaoId = votacaoId; }

    public Long getCondominoId() { return condominoId; }
    public void setCondominoId(Long condominoId) { this.condominoId = condominoId; }
}
