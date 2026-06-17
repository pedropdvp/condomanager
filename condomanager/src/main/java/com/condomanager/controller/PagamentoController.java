package com.condomanager.controller;

import com.condomanager.dto.PagamentoDTO;
import com.condomanager.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("@permissoes.pode('PAGAMENTOS','CONSULTAR')")
    public List<PagamentoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissoes.pode('PAGAMENTOS','CONSULTAR')")
    public PagamentoDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    @PreAuthorize("@permissoes.pode('PAGAMENTOS','CRIAR')")
    public ResponseEntity<PagamentoDTO> registar(@Valid @RequestBody PagamentoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registar(dto));
    }
}
