package com.condomanager.service;

import com.condomanager.dto.LoginRequest;
import com.condomanager.dto.LoginResponse;
import com.condomanager.dto.RegistoRequest;
import com.condomanager.dto.RegistoResponse;
import com.condomanager.model.Perfil;
import com.condomanager.model.PermissaoAcesso;
import com.condomanager.model.Utilizador;
import com.condomanager.model.enums.Acao;
import com.condomanager.model.enums.Funcionalidade;
import com.condomanager.repository.PerfilRepository;
import com.condomanager.repository.UtilizadorRepository;
import com.condomanager.security.JwtService;
import com.condomanager.security.MatrizPermissoes;
import com.condomanager.security.UtilizadorPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilizadorRepository utilizadorRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final MatrizPermissoes matrizPermissoes;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UtilizadorRepository utilizadorRepository,
                       PerfilRepository perfilRepository,
                       PasswordEncoder passwordEncoder,
                       MatrizPermissoes matrizPermissoes) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilizadorRepository = utilizadorRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
        this.matrizPermissoes = matrizPermissoes;
    }

    public LoginResponse autenticar(LoginRequest pedido) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(pedido.getEmail(), pedido.getPassword()));

        UtilizadorPrincipal principal = (UtilizadorPrincipal) auth.getPrincipal();
        String token = jwtService.gerarToken(principal);

        List<String> perfis = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

        return new LoginResponse(token, principal.getUsername(), principal.getUsername(), perfis);
    }

    /**
     * Cria um novo acesso (utilizador) com o perfil indicado.
     * A password e guardada com hash BCrypt (RNF01). Restrito a ADMIN_SISTEMA (ver AuthController).
     */
    @Transactional
    public RegistoResponse registar(RegistoRequest pedido) {
        if (utilizadorRepository.existsByEmail(pedido.getEmail())) {
            throw new IllegalArgumentException("Ja existe um utilizador com este email.");
        }

        Perfil perfil = perfilRepository.findByNome(pedido.getPerfil())
                .orElseThrow(() -> new IllegalArgumentException("Perfil invalido."));

        Set<PermissaoAcesso> permissoes = construirPermissoes(pedido, perfil);

        Utilizador utilizador = new Utilizador();
        utilizador.setNome(pedido.getNome());
        utilizador.setEmail(pedido.getEmail());
        utilizador.setPassword(passwordEncoder.encode(pedido.getPassword()));
        utilizador.setAtivo(true);
        utilizador.setPerfis(Set.of(perfil));
        utilizador.setPermissoes(permissoes);

        Utilizador guardado = utilizadorRepository.save(utilizador);

        return new RegistoResponse(guardado.getId(), guardado.getNome(), guardado.getEmail(),
                List.of(perfil.getNome().name()), resumirPermissoes(permissoes));
    }

    /**
     * Constroi as permissoes do novo utilizador: usa as escolhidas no pedido;
     * se nenhuma for indicada, aplica as permissoes-padrao do perfil (matriz).
     */
    private Set<PermissaoAcesso> construirPermissoes(RegistoRequest pedido, Perfil perfil) {
        Set<PermissaoAcesso> resultado = new HashSet<>();

        if (pedido.getPermissoes() != null && !pedido.getPermissoes().isEmpty()) {
            pedido.getPermissoes().forEach((funcionalidade, acoes) -> {
                if (funcionalidade != null && acoes != null) {
                    for (Acao acao : acoes) {
                        if (acao != null) resultado.add(new PermissaoAcesso(funcionalidade, acao));
                    }
                }
            });
        } else {
            for (Funcionalidade funcionalidade : Funcionalidade.values()) {
                for (Acao acao : matrizPermissoes.acoesPadrao(funcionalidade, perfil.getNome())) {
                    resultado.add(new PermissaoAcesso(funcionalidade, acao));
                }
            }
        }
        return resultado;
    }

    private Map<String, List<String>> resumirPermissoes(Set<PermissaoAcesso> permissoes) {
        Map<String, List<String>> resumo = new LinkedHashMap<>();
        for (PermissaoAcesso p : permissoes) {
            resumo.computeIfAbsent(p.getFuncionalidade().name(), k -> new ArrayList<>()).add(p.getAcao().name());
        }
        return resumo;
    }
}
