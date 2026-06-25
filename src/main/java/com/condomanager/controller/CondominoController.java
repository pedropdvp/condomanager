package com.condomanager.controller;

import com.condomanager.dto.CondominoCreateDTO;
import com.condomanager.dto.CondominoResponse;
import com.condomanager.dto.CondominoUpdateDTO;
import com.condomanager.dto.PageResponse;
import com.condomanager.service.CondominoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Gestão de condóminos no âmbito do tenant.
 */
@RestController
@RequestMapping("/api/v1/condominos")
public class CondominoController {

    private final CondominoService service;

    public CondominoController(CondominoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("@permissaoService.pode('CONDOMINOS', 'CRIAR')")
    public ResponseEntity<CondominoResponse> criar(@Valid @RequestBody CondominoCreateDTO dto,
                                                   UriComponentsBuilder uriBuilder) {
        CondominoResponse criado = service.criar(dto);
        URI location = uriBuilder.path("/api/v1/condominos/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    @PreAuthorize("@permissaoService.pode('CONDOMINOS', 'CONSULTAR')")
    public PageResponse<CondominoResponse> listar(@RequestParam(required = false) Long fracaoId,
                                                  Pageable pageable) {
        return PageResponse.de(service.listar(fracaoId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('CONDOMINOS', 'CONSULTAR')")
    public CondominoResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('CONDOMINOS', 'EDITAR')")
    public CondominoResponse atualizar(@PathVariable Long id, @Valid @RequestBody CondominoUpdateDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('CONDOMINOS', 'APAGAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
