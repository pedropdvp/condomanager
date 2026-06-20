package com.condomanager.configuration;

import com.condomanager.model.Perfil;
import com.condomanager.model.PermissaoAcesso;
import com.condomanager.model.Utilizador;
import com.condomanager.model.enums.Acao;
import com.condomanager.model.enums.Funcionalidade;
import com.condomanager.model.enums.NomePerfil;
import com.condomanager.repository.PerfilRepository;
import com.condomanager.repository.UtilizadorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

/**
 * Popula os perfis base e um Administrador do Sistema no primeiro arranque.
 * Credenciais por omissao: admin@condomanager.com / admin123 (alterar em producao).
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seed(PerfilRepository perfilRepository,
                                  UtilizadorRepository utilizadorRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            for (NomePerfil nome : NomePerfil.values()) {
                perfilRepository.findByNome(nome)
                        .orElseGet(() -> perfilRepository.save(new Perfil(nome, nome.name())));
            }

            String adminEmail = "admin@condomanager.com";
            Perfil adminPerfil = perfilRepository.findByNome(NomePerfil.ADMIN_SISTEMA).orElseThrow();

            // Administrador do Sistema: acesso total (todas as acoes em todas as funcionalidades)
            Set<PermissaoAcesso> todas = new HashSet<>();
            for (Funcionalidade f : Funcionalidade.values()) {
                for (Acao a : Acao.values()) {
                    todas.add(new PermissaoAcesso(f, a));
                }
            }

            Utilizador admin = utilizadorRepository.findByEmail(adminEmail).orElseGet(() -> {
                Utilizador novo = new Utilizador();
                novo.setNome("Administrador do Sistema");
                novo.setEmail(adminEmail);
                novo.setPassword(passwordEncoder.encode("admin123"));
                novo.setAtivo(true);
                return novo;
            });

            // Garante (de forma idempotente) que o admin tem sempre o perfil e o
            // acesso total. Corrige contas criadas antes do sistema de permissoes
            // granulares, que de outro modo receberiam HTTP 403 ao criar registos.
            boolean alterado = false;
            if (admin.getPerfis() == null || !admin.getPerfis().contains(adminPerfil)) {
                admin.setPerfis(Set.of(adminPerfil));
                alterado = true;
            }
            if (admin.getId() == null || !admin.getPermissoes().containsAll(todas)) {
                admin.setPermissoes(todas);
                alterado = true;
            }
            if (alterado) {
                utilizadorRepository.save(admin);
            }
        };
    }
}
