package com.condomanager.controller;

import com.condomanager.security.TenantContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Endpoint mínimo de verificação de saúde, usado para validar a fundação (Fase 0).
 *
 * <p>Devolve também o tenant resolvido para o pedido atual, o que permite confirmar
 * que o {@code TenantFilter} está a funcionar (envie o cabeçalho {@code X-Tenant-Id}).</p>
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "UP");
        body.put("service", "condomanager");
        body.put("tenant", TenantContext.getTenantId());
        return body;
    }
}
