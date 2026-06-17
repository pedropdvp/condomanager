package com.condomanager.controller;

import com.condomanager.dto.MensagemDTO;
import com.condomanager.service.MensagemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensagens")
public class MensagemController {

    private final MensagemService service;

    public MensagemController(MensagemService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public List<MensagemDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public MensagemDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO')")
    public ResponseEntity<MensagemDTO> enviar(@Valid @RequestBody MensagemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviar(dto));
    }
}
