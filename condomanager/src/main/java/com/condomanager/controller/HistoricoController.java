package com.condomanager.controller;

import com.condomanager.model.Historico;
import com.condomanager.service.AuditoriaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Consulta do historico de atividades (RF17).
 */
@RestController
@RequestMapping("/api/historico")
public class HistoricoController {

    private final AuditoriaService auditoriaService;

    public HistoricoController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public List<Historico> listar() {
        return auditoriaService.listar();
    }
}
