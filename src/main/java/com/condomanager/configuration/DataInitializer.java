package com.condomanager.configuration;

import com.condomanager.model.EmpresaGestao;
import com.condomanager.model.Perfil;
import com.condomanager.model.PerfilTipo;
import com.condomanager.model.Utilizador;
import com.condomanager.repository.EmpresaGestaoRepository;
import com.condomanager.repository.PerfilRepository;
import com.condomanager.repository.UtilizadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Semeia dados de desenvolvimento para permitir testar a autenticação e o isolamento
 * multi-tenant. <strong>Nunca</strong> corre fora do perfil {@code dev}.
 *
 * <p>Cria um {@code ADMIN_SISTEMA} e duas empresas demo, cada uma com o seu
 * {@code GESTOR_EMPRESA}, para evidenciar que um gestor só vê os condomínios da sua
 * própria empresa.</p>
 */
@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private static final String ADMIN_EMAIL = "admin@condomanager.local";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String GESTOR_PASSWORD = "gestor123";

    private final UtilizadorRepository utilizadorRepository;
    private final PerfilRepository perfilRepository;
    private final EmpresaGestaoRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilizadorRepository utilizadorRepository,
                           PerfilRepository perfilRepository,
                           EmpresaGestaoRepository empresaRepository,
                           PasswordEncoder passwordEncoder) {
        this.utilizadorRepository = utilizadorRepository;
        this.perfilRepository = perfilRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        criarAdminSistema();
        criarEmpresaComGestor("Empresa Alfa", "510000001", "alfa@demo.local", "gestor.alfa@demo.local");
        criarEmpresaComGestor("Empresa Beta", "510000002", "beta@demo.local", "gestor.beta@demo.local");
    }

    private void criarAdminSistema() {
        if (utilizadorRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }
        Utilizador admin = new Utilizador();
        admin.setNome("Administrador do Sistema");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setAtivo(true);
        admin.setIdEmpresa(null); // ADMIN_SISTEMA não pertence a nenhum tenant
        admin.adicionarPerfil(perfil(PerfilTipo.ADMIN_SISTEMA));
        utilizadorRepository.save(admin);
        logger.warn("[DEV] ADMIN_SISTEMA criado: {} / {}", ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    private void criarEmpresaComGestor(String nomeEmpresa, String nif, String emailEmpresa, String emailGestor) {
        if (utilizadorRepository.existsByEmail(emailGestor)) {
            return;
        }
        EmpresaGestao empresa = new EmpresaGestao();
        empresa.setNome(nomeEmpresa);
        empresa.setNif(nif);
        empresa.setEmail(emailEmpresa);
        empresa = empresaRepository.save(empresa);

        Utilizador gestor = new Utilizador();
        gestor.setNome("Gestor " + nomeEmpresa);
        gestor.setEmail(emailGestor);
        gestor.setPassword(passwordEncoder.encode(GESTOR_PASSWORD));
        gestor.setAtivo(true);
        gestor.setIdEmpresa(empresa.getId());
        gestor.adicionarPerfil(perfil(PerfilTipo.GESTOR_EMPRESA));
        utilizadorRepository.save(gestor);

        logger.warn("[DEV] {} (id={}) + gestor {} / {}", nomeEmpresa, empresa.getId(), emailGestor, GESTOR_PASSWORD);
    }

    private Perfil perfil(PerfilTipo tipo) {
        return perfilRepository.findByNome(tipo.name())
                .orElseThrow(() -> new IllegalStateException(
                        "Perfil " + tipo.name() + " em falta — verifique a migração V2."));
    }
}
