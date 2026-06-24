package com.condomanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dados para atualizar uma reunião (o condomínio e o estado são geridos à parte).
 */
public record ReuniaoUpdateDTO(

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        @JsonFormat(pattern = "HH:mm")
        LocalTime hora,

        @Size(max = 255)
        String local,

        String ordemTrabalhos
) {
}
