package com.condomanager.controller;

import com.condomanager.dto.PageResponse;
import com.condomanager.dto.PagamentoCreateDTO;
import com.condomanager.dto.PagamentoResponse;
import com.condomanager.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Registo e consulta de pagamentos no âmbito do tenant.
 */
@RestController
@RequestMapping("/api/v1/pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<PagamentoResponse> registar(@Valid @RequestBody PagamentoCreateDTO dto,
                                                      UriComponentsBuilder uriBuilder) {
        PagamentoResponse criado = service.registar(dto);
        URI location = uriBuilder.path("/api/v1/pagamentos/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public PageResponse<PagamentoResponse> listar(@RequestParam(required = false) Long quotaId,
                                                  Pageable pageable) {
        return PageResponse.de(service.listar(quotaId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public PagamentoResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }
}
