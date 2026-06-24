package com.condomanager.service;

import com.condomanager.dto.HistoricoResponse;
import com.condomanager.model.Historico;
import com.condomanager.repository.HistoricoRepository;
import com.condomanager.security.AuthenticatedUser;
import com.condomanager.security.SecurityUtils;
import com.condomanager.security.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Auditoria: escreve o histórico (append-only) e disponibiliza a sua consulta.
 */
@Service
public class AuditoriaService {

    private static final String ANONIMO = "anónimo";

    private final HistoricoRepository repository;

    public AuditoriaService(HistoricoRepository repository) {
        this.repository = repository;
    }

    /** Regista uma operação inferindo o utilizador/empresa do contexto atual. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registar(String operacao) {
        String utilizador = SecurityUtils.utilizadorAtual()
                .map(AuthenticatedUser::email)
                .orElse(ANONIMO);
        registarEvento(utilizador, TenantContext.getTenantId(), operacao);
    }

    /** Regista uma operação com utilizador e empresa explícitos (ex.: LOGIN). */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registarEvento(String utilizador, Long idEmpresa, String operacao) {
        repository.save(new Historico(idEmpresa, utilizador, operacao, LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public Page<HistoricoResponse> listar(Pageable pageable) {
        Page<Historico> pagina = SecurityUtils.isAdminSistema()
                ? repository.findAllByOrderByDataHoraDesc(pageable)
                : repository.findByIdEmpresaOrderByDataHoraDesc(TenantContext.getTenantId(), pageable);
        return pagina.map(this::toResponse);
    }

    private HistoricoResponse toResponse(Historico h) {
        return new HistoricoResponse(h.getId(), h.getIdEmpresa(), h.getUtilizador(),
                h.getOperacao(), h.getDataHora());
    }
}
