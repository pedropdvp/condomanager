package com.condomanager.service;

import com.condomanager.dto.ContextoCondominoResponse;
import com.condomanager.dto.QuotaResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Condominio;
import com.condomanager.model.Condomino;
import com.condomanager.model.Fracao;
import com.condomanager.model.Utilizador;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.UtilizadorRepository;
import com.condomanager.security.AuthenticatedUser;
import com.condomanager.security.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Portal do Condómino (self-service): dados do próprio condómino autenticado.
 *
 * <p>Tudo é resolvido a partir do {@code idCondomino} do utilizador da sessão e
 * fica restrito à sua fração — o condómino nunca vê dados de terceiros.</p>
 */
@Service
public class MeService {

    private final UtilizadorRepository utilizadorRepository;
    private final CondominoRepository condominoRepository;
    private final QuotaService quotaService;

    public MeService(UtilizadorRepository utilizadorRepository,
                     CondominoRepository condominoRepository,
                     QuotaService quotaService) {
        this.utilizadorRepository = utilizadorRepository;
        this.condominoRepository = condominoRepository;
        this.quotaService = quotaService;
    }

    @Transactional(readOnly = true)
    public ContextoCondominoResponse contexto() {
        Condomino c = meuCondomino();
        Fracao f = c.getFracao();
        Condominio cond = f.getCondominio();
        return new ContextoCondominoResponse(c.getId(), c.getNome(),
                cond.getId(), cond.getNome(), f.getId(), f.getNumero());
    }

    /** As quotas da fração do condómino autenticado (scoped — só a sua fração). */
    @Transactional(readOnly = true)
    public List<QuotaResponse> minhasQuotas() {
        Condomino c = meuCondomino();
        return quotaService.listar(null, c.getFracao().getId(), null, Pageable.unpaged()).getContent();
    }

    private Condomino meuCondomino() {
        Long uid = SecurityUtils.utilizadorAtual().map(AuthenticatedUser::id)
                .orElseThrow(() -> new AccessDeniedException("Sem sessão ativa."));
        Utilizador u = utilizadorRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", uid));
        if (u.getIdCondomino() == null) {
            throw new ResourceNotFoundException("Condómino associado ao utilizador", uid);
        }
        return condominoRepository.findById(u.getIdCondomino())
                .orElseThrow(() -> new ResourceNotFoundException("Condómino", u.getIdCondomino()));
    }
}
