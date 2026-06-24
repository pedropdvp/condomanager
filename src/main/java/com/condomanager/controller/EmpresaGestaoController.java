package com.condomanager.controller;

import com.condomanager.dto.EmpresaCreateDTO;
import com.condomanager.dto.EmpresaResponse;
import com.condomanager.dto.EmpresaUpdateDTO;
import com.condomanager.dto.PageResponse;
import com.condomanager.service.EmpresaGestaoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Gestão das empresas (tenants).
 *
 * <p>Criação, listagem global e desativação estão reservadas ao {@code ADMIN_SISTEMA}.
 * A consulta/atualização de uma empresa específica é permitida ao administrador ou ao
 * gestor dessa mesma empresa (a posse é validada no serviço).</p>
 */
@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaGestaoController {

    private final EmpresaGestaoService service;

    public EmpresaGestaoController(EmpresaGestaoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<EmpresaResponse> criar(@Valid @RequestBody EmpresaCreateDTO dto,
                                                 UriComponentsBuilder uriBuilder) {
        EmpresaResponse criada = service.criar(dto);
        URI location = uriBuilder.path("/api/v1/empresas/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN_SISTEMA')")
    public PageResponse<EmpresaResponse> listar(Pageable pageable) {
        return PageResponse.de(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA', 'GESTOR_EMPRESA')")
    public EmpresaResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA', 'GESTOR_EMPRESA')")
    public EmpresaResponse atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaUpdateDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
