package com.condomanager.controller;

import com.condomanager.dto.AtaCreateDTO;
import com.condomanager.dto.AtaResponse;
import com.condomanager.dto.AtaUpdateDTO;
import com.condomanager.dto.PageResponse;
import com.condomanager.service.AtaService;
import com.condomanager.service.DocumentoService.ConteudoDocumento;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Gestão de atas no âmbito do tenant, com anexo de ficheiro opcional.
 */
@RestController
@RequestMapping("/api/v1/atas")
public class AtaController {

    private final AtaService service;

    public AtaController(AtaService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<AtaResponse> criar(@Valid @RequestBody AtaCreateDTO dto,
                                             UriComponentsBuilder uriBuilder) {
        AtaResponse criada = service.criar(dto);
        URI location = uriBuilder.path("/api/v1/atas/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO', 'CONDOMINO')")
    public PageResponse<AtaResponse> listar(@RequestParam(required = false) Long reuniaoId,
                                            @RequestParam(required = false) String titulo,
                                            Pageable pageable) {
        return PageResponse.de(service.listar(reuniaoId, titulo, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO', 'CONDOMINO')")
    public AtaResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public AtaResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtaUpdateDTO dto) {
        return service.atualizar(id, dto);
    }

    @PostMapping(value = "/{id}/ficheiro", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public AtaResponse anexarFicheiro(@PathVariable Long id, @RequestParam("ficheiro") MultipartFile ficheiro) {
        if (ficheiro.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ficheiro é obrigatório e não pode ser vazio.");
        }
        return service.anexarFicheiro(id, ficheiro);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO', 'CONDOMINO')")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        ConteudoDocumento conteudo = service.descarregar(id);
        String nomeFicheiro = StringUtils.cleanPath(conteudo.nome());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeFicheiro + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(conteudo.recurso());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
