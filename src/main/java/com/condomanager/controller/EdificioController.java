package com.condomanager.controller;

import com.condomanager.dto.EdificioCreateDTO;
import com.condomanager.dto.EdificioResponse;
import com.condomanager.dto.EdificioUpdateDTO;
import com.condomanager.dto.PageResponse;
import com.condomanager.service.EdificioService;
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
 * Gestão de edifícios no âmbito do tenant.
 */
@RestController
@RequestMapping("/api/v1/edificios")
public class EdificioController {

    private final EdificioService service;

    public EdificioController(EdificioService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<EdificioResponse> criar(@Valid @RequestBody EdificioCreateDTO dto,
                                                  UriComponentsBuilder uriBuilder) {
        EdificioResponse criado = service.criar(dto);
        URI location = uriBuilder.path("/api/v1/edificios/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public PageResponse<EdificioResponse> listar(@RequestParam(required = false) Long condominioId,
                                                 Pageable pageable) {
        return PageResponse.de(service.listar(condominioId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public EdificioResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public EdificioResponse atualizar(@PathVariable Long id, @Valid @RequestBody EdificioUpdateDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
