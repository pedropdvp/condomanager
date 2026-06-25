package com.condomanager.controller;

import com.condomanager.dto.AlterarPasswordDTO;
import com.condomanager.dto.AssociarCondominoDTO;
import com.condomanager.dto.PageResponse;
import com.condomanager.dto.UtilizadorCreateDTO;
import com.condomanager.dto.UtilizadorResponse;
import com.condomanager.dto.UtilizadorUpdateDTO;
import com.condomanager.service.UtilizadorService;
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
 * Gestão de utilizadores. Reservada a ADMIN_SISTEMA e GESTOR_EMPRESA; o serviço
 * garante o isolamento por empresa.
 */
@RestController
@RequestMapping("/api/v1/utilizadores")
public class UtilizadorController {

    private final UtilizadorService service;

    public UtilizadorController(UtilizadorService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("@permissaoService.pode('UTILIZADORES', 'CRIAR')")
    public ResponseEntity<UtilizadorResponse> criar(@Valid @RequestBody UtilizadorCreateDTO dto,
                                                    UriComponentsBuilder uriBuilder) {
        UtilizadorResponse criado = service.criar(dto);
        URI location = uriBuilder.path("/api/v1/utilizadores/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    @PreAuthorize("@permissaoService.pode('UTILIZADORES', 'CONSULTAR')")
    public PageResponse<UtilizadorResponse> listar(Pageable pageable) {
        return PageResponse.de(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('UTILIZADORES', 'CONSULTAR')")
    public UtilizadorResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('UTILIZADORES', 'EDITAR')")
    public UtilizadorResponse atualizar(@PathVariable Long id, @Valid @RequestBody UtilizadorUpdateDTO dto) {
        return service.atualizar(id, dto);
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("@permissaoService.pode('UTILIZADORES', 'EDITAR')")
    public ResponseEntity<Void> alterarPassword(@PathVariable Long id,
                                                @Valid @RequestBody AlterarPasswordDTO dto) {
        service.alterarPassword(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/condomino")
    @PreAuthorize("@permissaoService.pode('UTILIZADORES', 'EDITAR')")
    public UtilizadorResponse associarCondomino(@PathVariable Long id,
                                                @Valid @RequestBody AssociarCondominoDTO dto) {
        return service.associarCondomino(id, dto.condominoId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('UTILIZADORES', 'APAGAR')")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
