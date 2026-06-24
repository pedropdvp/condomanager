package com.condomanager.controller;

import com.condomanager.dto.FracaoCreateDTO;
import com.condomanager.dto.FracaoResponse;
import com.condomanager.dto.FracaoUpdateDTO;
import com.condomanager.dto.PageResponse;
import com.condomanager.service.FracaoService;
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
 * Gestão de frações no âmbito do tenant.
 */
@RestController
@RequestMapping("/api/v1/fracoes")
public class FracaoController {

    private final FracaoService service;

    public FracaoController(FracaoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<FracaoResponse> criar(@Valid @RequestBody FracaoCreateDTO dto,
                                                UriComponentsBuilder uriBuilder) {
        FracaoResponse criada = service.criar(dto);
        URI location = uriBuilder.path("/api/v1/fracoes/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public PageResponse<FracaoResponse> listar(@RequestParam(required = false) Long condominioId,
                                               @RequestParam(required = false) Long edificioId,
                                               Pageable pageable) {
        return PageResponse.de(service.listar(condominioId, edificioId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public FracaoResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public FracaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody FracaoUpdateDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
