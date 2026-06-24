package com.condomanager.service;

import com.condomanager.dto.VotoCreateDTO;
import com.condomanager.dto.VotoResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.VotacaoMapper;
import com.condomanager.model.Condomino;
import com.condomanager.model.EstadoVotacao;
import com.condomanager.model.TipoCondomino;
import com.condomanager.model.Votacao;
import com.condomanager.model.Voto;
import com.condomanager.dto.VotoProprioDTO;
import com.condomanager.model.Utilizador;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.UtilizadorRepository;
import com.condomanager.repository.VotacaoRepository;
import com.condomanager.repository.VotoRepository;
import com.condomanager.security.AuthenticatedUser;
import com.condomanager.security.SecurityUtils;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registo e consulta de votos. Aplica as regras: votação ABERTA, voto único por
 * condómino, apenas proprietários votam e o condómino tem de pertencer ao condomínio
 * da votação.
 */
@Service
public class VotoService {

    private static final Logger logger = LoggerFactory.getLogger(VotoService.class);

    private final VotoRepository repository;
    private final VotacaoRepository votacaoRepository;
    private final CondominoRepository condominoRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final VotacaoMapper mapper;

    public VotoService(VotoRepository repository,
                       VotacaoRepository votacaoRepository,
                       CondominoRepository condominoRepository,
                       UtilizadorRepository utilizadorRepository,
                       VotacaoMapper mapper) {
        this.repository = repository;
        this.votacaoRepository = votacaoRepository;
        this.condominoRepository = condominoRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.mapper = mapper;
    }

    /** Voto do próprio utilizador (perfil CONDOMINO) usando o condómino associado à conta. */
    @Transactional
    public VotoResponse votarProprio(Long votacaoId, VotoProprioDTO dto) {
        Long uid = SecurityUtils.utilizadorAtual()
                .map(AuthenticatedUser::id)
                .orElseThrow(() -> new IllegalArgumentException("Sem utilizador autenticado."));
        Utilizador utilizador = utilizadorRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", uid));
        if (utilizador.getIdCondomino() == null) {
            throw new IllegalArgumentException("A sua conta não está associada a um condómino.");
        }
        return votar(votacaoId, new VotoCreateDTO(utilizador.getIdCondomino(), dto.resposta()));
    }

    @Transactional
    public VotoResponse votar(Long votacaoId, VotoCreateDTO dto) {
        Long tenant = tenantObrigatorio();
        Votacao votacao = votacaoRepository.findByIdAndIdEmpresa(votacaoId, tenant)
                .orElseThrow(() -> new ResourceNotFoundException("Votação", votacaoId));
        if (votacao.getEstado() != EstadoVotacao.ABERTA) {
            throw new IllegalArgumentException("A votação não está aberta.");
        }
        Condomino condomino = condominoRepository.findByIdAndIdEmpresa(dto.condominoId(), tenant)
                .orElseThrow(() -> new ResourceNotFoundException("Condómino", dto.condominoId()));
        if (condomino.getTipo() != TipoCondomino.PROPRIETARIO) {
            throw new IllegalArgumentException("Apenas proprietários podem votar.");
        }
        Long condominioVotacao = votacao.getReuniao().getCondominio().getId();
        Long condominioCondomino = condomino.getFracao().getCondominio().getId();
        if (!condominioVotacao.equals(condominioCondomino)) {
            throw new IllegalArgumentException("O condómino não pertence ao condomínio da votação.");
        }
        if (repository.existsByVotacao_IdAndCondomino_Id(votacaoId, condomino.getId())) {
            throw new IllegalArgumentException("Este condómino já votou nesta votação.");
        }

        Voto voto = new Voto();
        voto.setVotacao(votacao);
        voto.setCondomino(condomino);
        voto.setResposta(dto.resposta());
        Voto guardado = repository.save(voto);
        logger.info("Voto registado: votacao={}, condomino={}, resposta={}",
                votacaoId, condomino.getId(), dto.resposta());
        return mapper.toVotoResponse(guardado);
    }

    @Transactional(readOnly = true)
    public Page<VotoResponse> listar(Long votacaoId, Pageable pageable) {
        return repository.findByVotacao_Id(votacaoId, pageable).map(mapper::toVotoResponse);
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }
}
