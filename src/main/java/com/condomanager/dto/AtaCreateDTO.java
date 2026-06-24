package com.condomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Dados para criar uma ata. O {@code reuniaoId} é opcional nesta fase (a ligação a
 * Reuniões é concretizada na Fase 12).
 */
public record AtaCreateDTO(

        Long reuniaoId,

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 200)
        String titulo,

        String descricao,

        @NotNull(message = "A data da reunião é obrigatória")
        LocalDate dataReuniao
) {
}
