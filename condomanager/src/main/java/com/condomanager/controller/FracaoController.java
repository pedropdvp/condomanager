package com.condomanager.controller;

import com.condomanager.dto.FracaoDTO;
import com.condomanager.service.FracaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fracoes")
@PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO')")
public class FracaoController {

    private final FracaoService service;

    public FracaoController(FracaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<FracaoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public FracaoDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    public ResponseEntity<FracaoDTO> criar(@Valid @RequestBody FracaoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public FracaoDTO atualizar(@PathVariable Long id, @Valid @RequestBody FracaoDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
