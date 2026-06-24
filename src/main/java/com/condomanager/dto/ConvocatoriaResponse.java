package com.condomanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Convocatória de uma reunião (modelo de leitura).
 *
 * <p>Segundo o regime da propriedade horizontal (Lei 8/2022), a convocatória deve ser
 * enviada com pelo menos 10 dias de antecedência e indicar dia, hora, local e ordem de
 * trabalhos (ver {@code docs/LEGAL_RULES.md} §4).</p>
 */
public record ConvocatoriaResponse(
        Long reuniaoId,
        Long condominioId,
        LocalDate data,
        @JsonFormat(pattern = "HH:mm") LocalTime hora,
        String local,
        String ordemTrabalhos,
        int antecedenciaMinimaDias,
        String observacaoLegal
) {
}
