package com.condomanager.controller;

import com.condomanager.dto.HistoricoResponse;
import com.condomanager.dto.PageResponse;
import com.condomanager.service.AuditoriaService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consulta do histórico de auditoria (apenas leitura — o histórico é imutável).
 */
@RestController
@RequestMapping("/api/v1/auditoria")
public class AuditoriaController {

    private final AuditoriaService service;

    public AuditoriaController(AuditoriaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA', 'GESTOR_EMPRESA')")
    public PageResponse<HistoricoResponse> listar(Pageable pageable) {
        return PageResponse.de(service.listar(pageable));
    }
}
