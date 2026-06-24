package com.condomanager.dto;

import com.condomanager.model.EstadoReuniao;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Representação pública de uma reunião.
 */
public record ReuniaoResponse(
        Long id,
        Long condominioId,
        LocalDate data,
        @JsonFormat(pattern = "HH:mm") LocalTime hora,
        String local,
        String ordemTrabalhos,
        EstadoReuniao estado,
        Long idEmpresa,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
