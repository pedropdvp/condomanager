package com.condomanager.configuration;

import com.condomanager.model.Perfil;
import com.condomanager.model.Utilizador;
import com.condomanager.model.enums.NomePerfil;
import com.condomanager.repository.PerfilRepository;
import com.condomanager.repository.UtilizadorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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
            if (!utilizadorRepository.existsByEmail(adminEmail)) {
                Perfil adminPerfil = perfilRepository.findByNome(NomePerfil.ADMIN_SISTEMA).orElseThrow();
                Utilizador admin = new Utilizador();
                admin.setNome("Administrador do Sistema");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setAtivo(true);
                admin.setPerfis(Set.of(adminPerfil));
                utilizadorRepository.save(admin);
            }
        };
    }
}
