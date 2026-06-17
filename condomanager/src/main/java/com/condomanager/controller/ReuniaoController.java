package com.condomanager.controller;

import com.condomanager.dto.ReuniaoDTO;
import com.condomanager.service.ReuniaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reunioes")
public class ReuniaoController {

    private final ReuniaoService service;

    public ReuniaoController(ReuniaoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public List<ReuniaoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public ReuniaoDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','ADMIN_CONDOMINIO')")
    public ResponseEntity<ReuniaoDTO> agendar(@Valid @RequestBody ReuniaoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agendar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','ADMIN_CONDOMINIO')")
    public ReuniaoDTO atualizar(@PathVariable Long id, @Valid @RequestBody ReuniaoDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
