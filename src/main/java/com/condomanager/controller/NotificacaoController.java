package com.condomanager.controller;

import com.condomanager.dto.LembreteResponse;
import com.condomanager.service.NotificacaoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Notificações — envio de lembretes de quotas em atraso.
 */
@RestController
@RequestMapping("/api/v1/notificacoes")
public class NotificacaoController {

    private final NotificacaoService service;

    public NotificacaoController(NotificacaoService service) {
        this.service = service;
    }

    /** Envia (manualmente) lembretes de quotas em atraso aos condóminos do condomínio. */
    @PostMapping("/lembretes-quotas")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public LembreteResponse lembretesQuotas(@RequestParam Long condominioId) {
        return service.lembretesQuotasAtraso(condominioId);
    }
}
