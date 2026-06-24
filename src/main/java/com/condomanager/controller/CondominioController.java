package com.condomanager.controller;

import com.condomanager.dto.CondominioCreateDTO;
import com.condomanager.dto.CondominioResponse;
import com.condomanager.dto.CondominioUpdateDTO;
import com.condomanager.dto.PageResponse;
import com.condomanager.service.CondominioService;
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
 * Gestão de condomínios, no âmbito da empresa (tenant) do utilizador autenticado.
 *
 * <p>Todas as operações são automaticamente limitadas ao tenant: um gestor só vê e
 * altera os condomínios da sua própria empresa.</p>
 */
@RestController
@RequestMapping("/api/v1/condominios")
public class CondominioController {

    private final CondominioService service;

    public CondominioController(CondominioService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<CondominioResponse> criar(@Valid @RequestBody CondominioCreateDTO dto,
                                                    UriComponentsBuilder uriBuilder) {
        CondominioResponse criado = service.criar(dto);
        URI location = uriBuilder.path("/api/v1/condominios/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public PageResponse<CondominioResponse> listar(Pageable pageable) {
        return PageResponse.de(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public CondominioResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public CondominioResponse atualizar(@PathVariable Long id, @Valid @RequestBody CondominioUpdateDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
