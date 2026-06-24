package com.condomanager.dto;

import com.condomanager.model.PrioridadeOcorrencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dados para registar uma ocorrência.
 */
public record OcorrenciaCreateDTO(

        @NotNull(message = "O condomínio é obrigatório")
        Long condominioId,

        /** Condómino que reporta (opcional). */
        Long condominoId,

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 255)
        String titulo,

        String descricao,

        @NotNull(message = "A prioridade é obrigatória")
        PrioridadeOcorrencia prioridade
) {
}
