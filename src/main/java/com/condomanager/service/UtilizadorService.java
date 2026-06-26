package com.condomanager.service;

import com.condomanager.dto.AlterarPasswordDTO;
import com.condomanager.dto.UtilizadorCreateDTO;
import com.condomanager.dto.UtilizadorResponse;
import com.condomanager.dto.UtilizadorUpdateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.UtilizadorMapper;
import com.condomanager.model.Perfil;
import com.condomanager.model.PerfilTipo;
import com.condomanager.model.Utilizador;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.EmpresaGestaoRepository;
import com.condomanager.repository.PerfilRepository;
import com.condomanager.repository.UtilizadorRepository;
import com.condomanager.security.SecurityUtils;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Gestão de utilizadores, com isolamento por empresa.
 *
 * <p>O {@code Utilizador} não é uma {@code TenantAwareEntity} (o login tem de funcionar
 * sem tenant no contexto), por isso o isolamento por empresa é aplicado explicitamente
 * aqui. Um {@code GESTOR_EMPRESA} só gere utilizadores da sua empresa e não pode atribuir
 * o perfil {@code ADMIN_SISTEMA}; o {@code ADMIN_SISTEMA} gere qualquer utilizador.</p>
 */
@Service
public class UtilizadorService {

    private static final String RECURSO = "Utilizador";

    private static final Logger logger = LoggerFactory.getLogger(UtilizadorService.class);

    private final UtilizadorRepository repository;
    private final PerfilRepository perfilRepository;
    private final EmpresaGestaoRepository empresaRepository;
    private final CondominoRepository condominoRepository;
    private final UtilizadorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UtilizadorService(UtilizadorRepository repository,
                             PerfilRepository perfilRepository,
                             EmpresaGestaoRepository empresaRepository,
                             CondominoRepository condominoRepository,
                             UtilizadorMapper mapper,
                             PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.perfilRepository = perfilRepository;
        this.empresaRepository = empresaRepository;
        this.condominoRepository = condominoRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UtilizadorResponse associarCondomino(Long utilizadorId, Long condominoId) {
        Utilizador utilizador = obterAcessivel(utilizadorId);
        if (utilizador.getIdEmpresa() == null) {
            throw new IllegalArgumentException("Só utilizadores de uma empresa podem ser associados a um condómino.");
        }
        condominoRepository.findByIdAndIdEmpresa(condominoId, utilizador.getIdEmpresa())
                .orElseThrow(() -> new ResourceNotFoundException("Condómino", condominoId));
        utilizador.setIdCondomino(condominoId);
        logger.info("Utilizador id={} associado ao condómino id={}", utilizadorId, condominoId);
        return mapper.toResponse(utilizador);
    }

    @Transactional
    public UtilizadorResponse criar(UtilizadorCreateDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Já existe um utilizador com o email " + dto.email());
        }
        Set<Perfil> perfis = resolverPerfis(dto.perfis());

        Utilizador utilizador = new Utilizador();
        utilizador.setNome(dto.nome());
        utilizador.setEmail(dto.email());
        utilizador.setPassword(passwordEncoder.encode(dto.password()));
        utilizador.setAtivo(true);
        utilizador.setIdEmpresa(resolverEmpresa(dto.idEmpresa()));
        utilizador.setPerfis(perfis);

        Utilizador guardado = repository.save(utilizador);
        logger.info("Utilizador criado: id={}, id_empresa={}", guardado.getId(), guardado.getIdEmpresa());
        return mapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public Page<UtilizadorResponse> listar(Pageable pageable) {
        Page<Utilizador> pagina = SecurityUtils.isAdminSistema()
                ? repository.findAll(pageable)
                : repository.findByIdEmpresa(tenantObrigatorio(), pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UtilizadorResponse obterPorId(Long id) {
        return mapper.toResponse(obterAcessivel(id));
    }

    @Transactional
    public UtilizadorResponse atualizar(Long id, UtilizadorUpdateDTO dto) {
        Utilizador utilizador = obterAcessivel(id);
        utilizador.setNome(dto.nome());
        utilizador.setAtivo(dto.ativo());
        utilizador.setPerfis(resolverPerfis(dto.perfis()));
        logger.info("Utilizador atualizado: id={}", id);
        return mapper.toResponse(utilizador);
    }

    @Transactional
    public void alterarPassword(Long id, AlterarPasswordDTO dto) {
        Utilizador utilizador = obterAcessivel(id);
        utilizador.setPassword(passwordEncoder.encode(dto.novaPassword()));
        logger.info("Password alterada: utilizador id={}", id);
    }

    @Transactional
    public void desativar(Long id) {
        Utilizador utilizador = obterAcessivel(id);
        utilizador.setAtivo(false);
        logger.info("Utilizador desativado: id={}", id);
    }

    /** Anonimiza os dados pessoais do utilizador e desativa a conta (direito ao apagamento, RGPD). */
    @Transactional
    public void anonimizar(Long id) {
        Utilizador utilizador = obterAcessivel(id);
        utilizador.setNome("(conta removida)");
        utilizador.setEmail("anon-" + id + "@removido.local");
        utilizador.setAtivo(false);
        logger.info("Conta anonimizada (RGPD): utilizador id={}", id);
    }

    /** Carrega o utilizador validando o acesso do utilizador atual (isolamento por empresa). */
    private Utilizador obterAcessivel(Long id) {
        Utilizador utilizador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
        if (!SecurityUtils.isAdminSistema()) {
            Long tenant = tenantObrigatorio();
            if (utilizador.getIdEmpresa() == null || !tenant.equals(utilizador.getIdEmpresa())) {
                throw new ResourceNotFoundException(RECURSO, id);
            }
        }
        return utilizador;
    }

    /** Determina a empresa do novo utilizador conforme o perfil do criador. */
    private Long resolverEmpresa(Long idEmpresaPedido) {
        if (!SecurityUtils.isAdminSistema()) {
            return tenantObrigatorio();
        }
        if (idEmpresaPedido == null) {
            return null; // ADMIN_SISTEMA pode criar outro utilizador sem empresa
        }
        if (!empresaRepository.existsById(idEmpresaPedido)) {
            throw new ResourceNotFoundException("Empresa de gestão", idEmpresaPedido);
        }
        return idEmpresaPedido;
    }

    private Set<Perfil> resolverPerfis(Set<String> nomes) {
        boolean admin = SecurityUtils.isAdminSistema();
        Set<Perfil> perfis = new HashSet<>();
        for (String nome : nomes) {
            if (PerfilTipo.ADMIN_SISTEMA.name().equals(nome) && !admin) {
                throw new AccessDeniedException("Não tem permissão para atribuir o perfil ADMIN_SISTEMA.");
            }
            Perfil perfil = perfilRepository.findByNome(nome)
                    .orElseThrow(() -> new IllegalArgumentException("Perfil inválido: " + nome));
            perfis.add(perfil);
        }
        return perfis;
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }
}
