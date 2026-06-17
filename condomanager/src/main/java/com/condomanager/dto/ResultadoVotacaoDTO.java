package com.condomanager.dto;

/**
 * Contagem automatica dos votos de uma votacao.
 */
public class ResultadoVotacaoDTO {

    private Long votacaoId;
    private String tema;
    private long sim;
    private long nao;
    private long abstencao;
    private long total;

    public ResultadoVotacaoDTO(Long votacaoId, String tema, long sim, long nao, long abstencao) {
        this.votacaoId = votacaoId;
        this.tema = tema;
        this.sim = sim;
        this.nao = nao;
        this.abstencao = abstencao;
        this.total = sim + nao + abstencao;
    }

    public Long getVotacaoId() { return votacaoId; }
    public String getTema() { return tema; }
    public long getSim() { return sim; }
    public long getNao() { return nao; }
    public long getAbstencao() { return abstencao; }
    public long getTotal() { return total; }
}
