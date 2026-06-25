package com.condomanager.controller;

import com.condomanager.dto.MensagemCreateDTO;
import com.condomanager.dto.MensagemResponse;
import com.condomanager.dto.PageResponse;
import com.condomanager.service.MensagemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

/**
 * Comunicação interna: envio e consulta de mensagens, no âmbito do tenant.
 */
@RestController
@RequestMapping("/api/v1/mensagens")
public class MensagemController {

    private final MensagemService service;

    public MensagemController(MensagemService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("@permissaoService.pode('MENSAGENS', 'CRIAR')")
    public ResponseEntity<MensagemResponse> enviar(@Valid @RequestBody MensagemCreateDTO dto,
                                                   UriComponentsBuilder uriBuilder) {
        MensagemResponse criada = service.enviar(dto);
        URI location = uriBuilder.path("/api/v1/mensagens/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping("/recebidas")
    @PreAuthorize("@permissaoService.pode('MENSAGENS', 'CONSULTAR')")
    public PageResponse<MensagemResponse> recebidas(Pageable pageable) {
        return PageResponse.de(service.caixaDeEntrada(pageable));
    }

    @GetMapping("/enviadas")
    @PreAuthorize("@permissaoService.pode('MENSAGENS', 'CONSULTAR')")
    public PageResponse<MensagemResponse> enviadas(Pageable pageable) {
        return PageResponse.de(service.enviadas(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('MENSAGENS', 'CONSULTAR')")
    public MensagemResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PutMapping("/{id}/lida")
    @PreAuthorize("@permissaoService.pode('MENSAGENS', 'CONSULTAR')")
    public MensagemResponse marcarLida(@PathVariable Long id) {
        return service.marcarComoLida(id);
    }

    @GetMapping("/nao-lidas/count")
    @PreAuthorize("@permissaoService.pode('MENSAGENS', 'CONSULTAR')")
    public Map<String, Long> naoLidas() {
        return Map.of("naoLidas", service.contarNaoLidas());
    }
}
