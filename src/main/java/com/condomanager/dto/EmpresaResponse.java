package com.condomanager.dto;

import com.condomanager.model.EstadoEmpresa;

import java.time.LocalDateTime;

/**
 * Representação pública de uma empresa de gestão.
 */
public record EmpresaResponse(
        Long id,
        String nome,
        String nif,
        String email,
        String telefone,
        String morada,
        EstadoEmpresa estado,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
