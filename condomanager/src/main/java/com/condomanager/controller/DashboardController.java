package com.condomanager.controller;

import com.condomanager.dto.DashboardDTO;
import com.condomanager.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public DashboardDTO indicadores() {
        return service.indicadores();
    }
}
