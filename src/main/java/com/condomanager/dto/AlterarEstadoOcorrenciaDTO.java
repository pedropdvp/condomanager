package com.condomanager.dto;

import com.condomanager.model.EstadoOcorrencia;
import jakarta.validation.constraints.NotNull;

/**
 * Dados para alterar o estado de uma ocorrência.
 */
public record AlterarEstadoOcorrenciaDTO(

        @NotNull(message = "O estado é obrigatório")
        EstadoOcorrencia estado
) {
}
