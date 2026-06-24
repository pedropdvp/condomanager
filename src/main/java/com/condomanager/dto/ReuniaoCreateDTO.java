package com.condomanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dados para agendar uma reunião.
 */
public record ReuniaoCreateDTO(

        @NotNull(message = "O condomínio é obrigatório")
        Long condominioId,

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        @JsonFormat(pattern = "HH:mm")
        LocalTime hora,

        @Size(max = 255)
        String local,

        String ordemTrabalhos
) {
}
