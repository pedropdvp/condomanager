package com.condomanager.dto;

import java.util.List;

/**
 * Representação pública de um utilizador (nunca expõe a password).
 */
public record UtilizadorResponse(
        Long id,
        String nome,
        String email,
        boolean ativo,
        Long idEmpresa,
        Long idCondomino,
        List<String> perfis
) {
}
