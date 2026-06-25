package com.condomanager.controller;

import com.condomanager.dto.ConvocatoriaEnvioResponse;
import com.condomanager.dto.ConvocatoriaResponse;
import com.condomanager.dto.PageResponse;
import com.condomanager.dto.ReuniaoCreateDTO;
import com.condomanager.dto.ReuniaoResponse;
import com.condomanager.dto.ReuniaoUpdateDTO;
import com.condomanager.model.EstadoReuniao;
import com.condomanager.service.ReuniaoService;
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
 * Gestão de reuniões no âmbito do tenant: agendamento, convocatória e estados.
 */
@RestController
@RequestMapping("/api/v1/reunioes")
public class ReuniaoController {

    private final ReuniaoService service;

    public ReuniaoController(ReuniaoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("@permissaoService.pode('REUNIOES', 'CRIAR')")
    public ResponseEntity<ReuniaoResponse> criar(@Valid @RequestBody ReuniaoCreateDTO dto,
                                                 UriComponentsBuilder uriBuilder) {
        ReuniaoResponse criada = service.criar(dto);
        URI location = uriBuilder.path("/api/v1/reunioes/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    @PreAuthorize("@permissaoService.pode('REUNIOES', 'CONSULTAR')")
    public PageResponse<ReuniaoResponse> listar(@RequestParam(required = false) Long condominioId,
                                                @RequestParam(required = false) EstadoReuniao estado,
                                                Pageable pageable) {
        return PageResponse.de(service.listar(condominioId, estado, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('REUNIOES', 'CONSULTAR')")
    public ReuniaoResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @GetMapping("/{id}/convocatoria")
    @PreAuthorize("@permissaoService.pode('REUNIOES', 'CONSULTAR')")
    public ConvocatoriaResponse convocatoria(@PathVariable Long id) {
        return service.convocatoria(id);
    }

    /** Envia a convocatória por email aos condóminos; devolve o resumo e a lista de destinatários. */
    @PostMapping("/{id}/convocar")
    @PreAuthorize("@permissaoService.pode('REUNIOES', 'EDITAR')")
    public ConvocatoriaEnvioResponse convocar(@PathVariable Long id) {
        return service.convocar(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('REUNIOES', 'EDITAR')")
    public ReuniaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ReuniaoUpdateDTO dto) {
        return service.atualizar(id, dto);
    }

    @PostMapping("/{id}/realizar")
    @PreAuthorize("@permissaoService.pode('REUNIOES', 'EDITAR')")
    public ReuniaoResponse realizar(@PathVariable Long id) {
        return service.marcarRealizada(id);
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("@permissaoService.pode('REUNIOES', 'EDITAR')")
    public ReuniaoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('REUNIOES', 'APAGAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
