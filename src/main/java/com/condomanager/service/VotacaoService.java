package com.condomanager.service;

import com.condomanager.dto.ResultadoVotacaoResponse;
import com.condomanager.dto.VotacaoCreateDTO;
import com.condomanager.dto.VotacaoResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.VotacaoMapper;
import com.condomanager.model.EstadoVotacao;
import com.condomanager.model.Fracao;
import com.condomanager.model.Reuniao;
import com.condomanager.model.TipoMaioria;
import com.condomanager.model.Votacao;
import com.condomanager.model.Voto;
import com.condomanager.repository.FracaoRepository;
import com.condomanager.repository.ReuniaoRepository;
import com.condomanager.repository.VotacaoRepository;
import com.condomanager.repository.VotoRepository;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ciclo de vida das votações e contagem por permilagem segundo as maiorias legais
 * (ver {@code docs/LEGAL_RULES.md} §6).
 */
@Service
public class VotacaoService {

    private static final String RECURSO = "Votação";

    private static final Logger logger = LoggerFactory.getLogger(VotacaoService.class);

    private final VotacaoRepository repository;
    private final ReuniaoRepository reuniaoRepository;
    private final VotoRepository votoRepository;
    private final FracaoRepository fracaoRepository;
    private final VotacaoMapper mapper;

    public VotacaoService(VotacaoRepository repository,
                          ReuniaoRepository reuniaoRepository,
                          VotoRepository votoRepository,
                          FracaoRepository fracaoRepository,
                          VotacaoMapper mapper) {
        this.repository = repository;
        this.reuniaoRepository = reuniaoRepository;
        this.votoRepository = votoRepository;
        this.fracaoRepository = fracaoRepository;
        this.mapper = mapper;
    }

    @Transactional
    public VotacaoResponse criar(VotacaoCreateDTO dto) {
        Long tenant = tenantObrigatorio();
        Reuniao reuniao = reuniaoRepository.findByIdAndIdEmpresa(dto.reuniaoId(), tenant)
                .orElseThrow(() -> new ResourceNotFoundException("Reunião", dto.reuniaoId()));
        Votacao guardada = repository.save(mapper.toEntity(dto, reuniao));
        logger.info("Votação criada: id={}, id_reuniao={}", guardada.getId(), reuniao.getId());
        return mapper.toResponse(guardada);
    }

    @Transactional
    public VotacaoResponse abrir(Long id) {
        Votacao votacao = obterDoTenant(id);
        if (votacao.getEstado() != EstadoVotacao.CRIADA) {
            throw new IllegalArgumentException("Só é possível abrir uma votação CRIADA.");
        }
        votacao.setEstado(EstadoVotacao.ABERTA);
        logger.info("Votação aberta: id={}", id);
        return mapper.toResponse(votacao);
    }

    @Transactional
    public VotacaoResponse encerrar(Long id) {
        Votacao votacao = obterDoTenant(id);
        if (votacao.getEstado() != EstadoVotacao.ABERTA) {
            throw new IllegalArgumentException("Só é possível encerrar uma votação ABERTA.");
        }
        votacao.setEstado(EstadoVotacao.ENCERRADA);
        logger.info("Votação encerrada: id={}", id);
        return mapper.toResponse(votacao);
    }

    @Transactional(readOnly = true)
    public Page<VotacaoResponse> listar(Long reuniaoId, EstadoVotacao estado, Pageable pageable) {
        Page<Votacao> pagina;
        if (reuniaoId != null) {
            pagina = repository.findByReuniao_Id(reuniaoId, pageable);
        } else if (estado != null) {
            pagina = repository.findByEstado(estado, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VotacaoResponse obterPorId(Long id) {
        return mapper.toResponse(obterDoTenant(id));
    }

    /**
     * Conta os votos (ponderados pela permilagem) e aplica a maioria exigida.
     */
    @Transactional(readOnly = true)
    public ResultadoVotacaoResponse resultado(Long id) {
        Votacao votacao = obterDoTenant(id);
        Long condominioId = votacao.getReuniao().getCondominio().getId();

        BigDecimal capitalTotal = fracaoRepository.findByCondominio_Id(condominioId).stream()
                .map(Fracao::getPermilagem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Voto> votos = votoRepository.findByVotacao_Id(id);
        BigDecimal somaSim = BigDecimal.ZERO;
        BigDecimal somaNao = BigDecimal.ZERO;
        BigDecimal somaAbst = BigDecimal.ZERO;
        for (Voto voto : votos) {
            BigDecimal peso = voto.getCondomino().getFracao().getPermilagem();
            switch (voto.getResposta()) {
                case SIM -> somaSim = somaSim.add(peso);
                case NAO -> somaNao = somaNao.add(peso);
                case ABSTENCAO -> somaAbst = somaAbst.add(peso);
            }
        }
        BigDecimal capitalPresente = somaSim.add(somaNao).add(somaAbst);
        boolean aprovado = avaliar(votacao.getTipoMaioria(), somaSim, somaNao, capitalPresente, capitalTotal);

        return new ResultadoVotacaoResponse(
                votacao.getId(), votacao.getTema(), votacao.getTipoMaioria(), votacao.getEstado(),
                capitalTotal, capitalPresente, somaSim, somaNao, somaAbst, votos.size(), aprovado);
    }

    private boolean avaliar(TipoMaioria tipo, BigDecimal somaSim, BigDecimal somaNao,
                            BigDecimal capitalPresente, BigDecimal capitalTotal) {
        return switch (tipo) {
            // Mais de 50% do capital presente: 2*sim > presente
            case MAIORIA_SIMPLES -> somaSim.multiply(BigDecimal.TWO).compareTo(capitalPresente) > 0;
            // Maioria do capital presente e zero votos contra
            case SEM_OPOSICAO -> somaNao.signum() == 0
                    && somaSim.multiply(BigDecimal.TWO).compareTo(capitalPresente) > 0;
            // Pelo menos 2/3 do capital total: 3*sim >= 2*total
            case DOIS_TERCOS -> somaSim.multiply(BigDecimal.valueOf(3))
                    .compareTo(capitalTotal.multiply(BigDecimal.TWO)) >= 0;
            // 100% do capital total
            case UNANIMIDADE -> capitalTotal.signum() > 0 && somaSim.compareTo(capitalTotal) >= 0;
        };
    }

    @Transactional
    public void eliminar(Long id) {
        Votacao votacao = obterDoTenant(id);
        votoRepository.deleteAll(votoRepository.findByVotacao_Id(id));
        repository.delete(votacao);
    }

    Votacao obterDoTenant(Long id) {
        return repository.findByIdAndIdEmpresa(id, tenantObrigatorio())
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }
}
