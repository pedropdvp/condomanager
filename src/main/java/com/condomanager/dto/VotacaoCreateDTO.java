package com.condomanager.dto;

import com.condomanager.model.TipoMaioria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Dados para criar uma votação associada a uma reunião.
 */
public record VotacaoCreateDTO(

        @NotNull(message = "A reunião é obrigatória")
        Long reuniaoId,

        @NotBlank(message = "O tema é obrigatório")
        @Size(max = 255)
        String tema,

        LocalDateTime dataInicio,

        LocalDateTime dataFim,

        @NotNull(message = "O tipo de maioria é obrigatório")
        TipoMaioria tipoMaioria
) {
}
