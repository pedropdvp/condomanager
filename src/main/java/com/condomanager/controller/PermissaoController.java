package com.condomanager.controller;

import com.condomanager.model.Acao;
import com.condomanager.model.Funcionalidade;
import com.condomanager.model.PerfilTipo;
import com.condomanager.service.PermissaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gestão da matriz de permissões granulares por perfil (RBAC).
 *
 * <ul>
 *   <li>{@code GET /me} — permissões efetivas do utilizador autenticado (a UI usa para guiar o que mostra).</li>
 *   <li>{@code GET /perfis} — perfis editáveis (exclui ADMIN_SISTEMA).</li>
 *   <li>{@code GET /{perfil}} — matriz de um perfil.</li>
 *   <li>{@code PUT /{perfil}} — substitui a matriz de um perfil (só ADMIN_SISTEMA).</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/permissoes")
public class PermissaoController {

    private final PermissaoService service;

    public PermissaoController(PermissaoService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public Map<Funcionalidade, List<Acao>> minhas() {
        return service.minhasPermissoes();
    }

    @GetMapping("/perfis")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA', 'GESTOR_EMPRESA')")
    public List<String> perfisEditaveis() {
        return Arrays.stream(PerfilTipo.values())
                .filter(p -> p != PerfilTipo.ADMIN_SISTEMA)
                .map(Enum::name)
                .toList();
    }

    @GetMapping("/{perfil}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA', 'GESTOR_EMPRESA')")
    public Map<Funcionalidade, List<Acao>> matriz(@PathVariable String perfil) {
        return service.matriz(perfil);
    }

    @PutMapping("/{perfil}")
    @PreAuthorize("hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<Void> atualizar(@PathVariable String perfil,
                                          @RequestBody Map<String, List<String>> body) {
        Map<Funcionalidade, Set<Acao>> nova = new EnumMap<>(Funcionalidade.class);
        body.forEach((func, acoes) -> nova.put(
                Funcionalidade.valueOf(func),
                acoes.stream().map(Acao::valueOf).collect(Collectors.toSet())));
        service.atualizar(perfil, nova);
        return ResponseEntity.noContent().build();
    }
}
