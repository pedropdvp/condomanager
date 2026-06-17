package com.condomanager.controller;

import com.condomanager.dto.CondominoDTO;
import com.condomanager.service.CondominoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/condominos")
@PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO')")
public class CondominoController {

    private final CondominoService service;

    public CondominoController(CondominoService service) {
        this.service = service;
    }

    @GetMapping
    public List<CondominoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public CondominoDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    public ResponseEntity<CondominoDTO> criar(@Valid @RequestBody CondominoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public CondominoDTO atualizar(@PathVariable Long id, @Valid @RequestBody CondominoDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO')")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
