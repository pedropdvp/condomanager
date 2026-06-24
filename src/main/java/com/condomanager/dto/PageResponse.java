package com.condomanager.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Resposta paginada com formato estável (evita serializar diretamente {@code Page}).
 */
public record PageResponse<T>(
        List<T> conteudo,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas
) {
    public static <T> PageResponse<T> de(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
