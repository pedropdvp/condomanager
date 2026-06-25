package com.condomanager.service;

import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Acao;
import com.condomanager.model.Funcionalidade;
import com.condomanager.model.Perfil;
import com.condomanager.model.PerfilTipo;
import com.condomanager.model.Permissao;
import com.condomanager.repository.PerfilRepository;
import com.condomanager.repository.PermissaoRepository;
import com.condomanager.security.AuthenticatedUser;
import com.condomanager.security.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Permissões granulares por perfil (RBAC com matriz funcionalidade × ação).
 *
 * <p>O método {@link #pode(String, String)} é usado nas anotações
 * {@code @PreAuthorize("@permissaoService.pode('CONDOMINIOS','CRIAR')")}.
 * O {@code ADMIN_SISTEMA} tem acesso total (não depende da tabela).</p>
 */
@Service("permissaoService")
public class PermissaoService {

    private static final String ADMIN = PerfilTipo.ADMIN_SISTEMA.name();

    private final PermissaoRepository repository;
    private final PerfilRepository perfilRepository;

    public PermissaoService(PermissaoRepository repository, PerfilRepository perfilRepository) {
        this.repository = repository;
        this.perfilRepository = perfilRepository;
    }

    /** Verifica se o utilizador autenticado pode executar a ação na funcionalidade. */
    @Transactional(readOnly = true)
    public boolean pode(String funcionalidade, String acao) {
        Set<String> perfis = perfisAtuais();
        if (perfis.isEmpty()) {
            return false;
        }
        if (perfis.contains(ADMIN)) {
            return true; // acesso total
        }
        try {
            return repository.existsByPerfil_NomeInAndFuncionalidadeAndAcao(
                    perfis, Funcionalidade.valueOf(funcionalidade), Acao.valueOf(acao));
        } catch (IllegalArgumentException e) {
            return false; // funcionalidade/ação desconhecida
        }
    }

    /**
     * Permite a ação se o alvo for o <strong>próprio</strong> utilizador autenticado, ou então
     * se tiver a permissão indicada. Usado em operações *self-service* (ex.: alterar a própria
     * password) que não devem exigir permissão de gestão de utilizadores.
     */
    @Transactional(readOnly = true)
    public boolean podeGerirOuProprio(Long idAlvo, String funcionalidade, String acao) {
        Long meu = SecurityUtils.utilizadorAtual().map(AuthenticatedUser::id).orElse(null);
        if (meu != null && meu.equals(idAlvo)) {
            return true;
        }
        return pode(funcionalidade, acao);
    }

    /** Matriz de permissões de um perfil: funcionalidade → ações permitidas. */
    @Transactional(readOnly = true)
    public Map<Funcionalidade, List<Acao>> matriz(String perfil) {
        if (ADMIN.equals(perfil)) {
            return matrizTotal();
        }
        Map<Funcionalidade, List<Acao>> m = new EnumMap<>(Funcionalidade.class);
        for (Permissao p : repository.findByPerfil_Nome(perfil)) {
            m.computeIfAbsent(p.getFuncionalidade(), k -> new ArrayList<>()).add(p.getAcao());
        }
        return m;
    }

    /** Permissões efetivas do utilizador autenticado (união dos seus perfis). */
    @Transactional(readOnly = true)
    public Map<Funcionalidade, List<Acao>> minhasPermissoes() {
        Set<String> perfis = perfisAtuais();
        if (perfis.contains(ADMIN)) {
            return matrizTotal();
        }
        Map<Funcionalidade, List<Acao>> m = new EnumMap<>(Funcionalidade.class);
        for (Permissao p : repository.findByPerfil_NomeIn(perfis)) {
            m.computeIfAbsent(p.getFuncionalidade(), k -> new ArrayList<>())
                    .removeIf(a -> a == p.getAcao()); // evitar duplicados
            m.get(p.getFuncionalidade()).add(p.getAcao());
        }
        return m;
    }

    /** Substitui toda a matriz de um perfil. O {@code ADMIN_SISTEMA} não é editável. */
    @Transactional
    public void atualizar(String perfil, Map<Funcionalidade, Set<Acao>> nova) {
        if (ADMIN.equals(perfil)) {
            throw new IllegalArgumentException("O ADMIN_SISTEMA tem acesso total e não é editável.");
        }
        Perfil p = perfilRepository.findByNome(perfil)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil", perfil));
        repository.deleteByPerfil_Nome(perfil);
        repository.flush();
        List<Permissao> novas = new ArrayList<>();
        nova.forEach((func, acoes) -> acoes.forEach(acao -> {
            Permissao perm = new Permissao();
            perm.setPerfil(p);
            perm.setFuncionalidade(func);
            perm.setAcao(acao);
            novas.add(perm);
        }));
        repository.saveAll(novas);
    }

    private Map<Funcionalidade, List<Acao>> matrizTotal() {
        Map<Funcionalidade, List<Acao>> all = new EnumMap<>(Funcionalidade.class);
        for (Funcionalidade f : Funcionalidade.values()) {
            all.put(f, List.of(Acao.values()));
        }
        return all;
    }

    private Set<String> perfisAtuais() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Set.of();
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith(PerfilTipo.PREFIXO_ROLE))
                .map(a -> a.substring(PerfilTipo.PREFIXO_ROLE.length()))
                .collect(Collectors.toSet());
    }
}
