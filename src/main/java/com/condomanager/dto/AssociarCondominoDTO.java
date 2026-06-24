package com.condomanager.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Associa uma conta de utilizador a um registo de condómino.
 */
public record AssociarCondominoDTO(

        @NotNull(message = "O condómino é obrigatório")
        Long condominoId
) {
}
