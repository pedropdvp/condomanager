package com.condomanager.service;

import com.condomanager.dto.GeracaoQuotasResultado;
import com.condomanager.dto.GerarQuotasDTO;
import com.condomanager.dto.QuotaResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.QuotaMapper;
import com.condomanager.model.Condominio;
import com.condomanager.model.EstadoQuota;
import com.condomanager.model.Fracao;
import com.condomanager.model.Quota;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.FracaoRepository;
import com.condomanager.repository.QuotaRepository;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Regras de negócio das quotas, incluindo a geração mensal por permilagem.
 *
 * <p>{@code valor = orcamento_anual × (permilagem / 1000) / 12}, arredondado a 2 casas
 * (ver {@code docs/LEGAL_RULES.md} §2).</p>
 */
@Service
public class QuotaService {

    private static final String RECURSO = "Quota";
    /** orçamento × permilagem / 1000 (milésimos) / 12 (meses) = / 12000. */
    private static final BigDecimal DIVISOR_MENSAL = new BigDecimal("12000");

    private static final Logger logger = LoggerFactory.getLogger(QuotaService.class);

    private final QuotaRepository repository;
    private final CondominioRepository condominioRepository;
    private final FracaoRepository fracaoRepository;
    private final QuotaMapper mapper;

    public QuotaService(QuotaRepository repository,
                        CondominioRepository condominioRepository,
                        FracaoRepository fracaoRepository,
                        QuotaMapper mapper) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
        this.fracaoRepository = fracaoRepository;
        this.mapper = mapper;
    }

    @Transactional
    public GeracaoQuotasResultado gerar(GerarQuotasDTO dto) {
        Long tenant = tenantObrigatorio();
        Condominio condominio = condominioRepository.findByIdAndIdEmpresa(dto.condominioId(), tenant)
                .orElseThrow(() -> new ResourceNotFoundException("Condomínio", dto.condominioId()));

        List<Fracao> fracoes = fracaoRepository.findByCondominio_Id(condominio.getId());
        int geradas = 0;
        int ignoradas = 0;
        BigDecimal total = BigDecimal.ZERO;

        for (Fracao fracao : fracoes) {
            if (repository.existsByFracao_IdAndMesAndAno(fracao.getId(), dto.mes(), dto.ano())) {
                ignoradas++;
                continue;
            }
            BigDecimal valor = calcularValor(condominio.getOrcamentoAnual(), fracao.getPermilagem());

            Quota quota = new Quota();
            quota.setFracao(fracao);
            quota.setMes(dto.mes());
            quota.setAno(dto.ano());
            quota.setValor(valor);
            quota.setEstado(EstadoQuota.PENDENTE);
            repository.save(quota);

            geradas++;
            total = total.add(valor);
        }

        logger.info("Geração de quotas {}/{} para condomínio {}: {} geradas, {} ignoradas",
                dto.mes(), dto.ano(), condominio.getId(), geradas, ignoradas);
        return new GeracaoQuotasResultado(dto.mes(), dto.ano(), geradas, ignoradas, total);
    }

    private BigDecimal calcularValor(BigDecimal orcamentoAnual, BigDecimal permilagem) {
        return orcamentoAnual.multiply(permilagem)
                .divide(DIVISOR_MENSAL, 2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public Page<QuotaResponse> listar(Long condominioId, Long fracaoId, EstadoQuota estado, Pageable pageable) {
        Page<Quota> pagina;
        if (fracaoId != null) {
            pagina = repository.findByFracao_Id(fracaoId, pageable);
        } else if (condominioId != null) {
            pagina = repository.findByFracao_Condominio_Id(condominioId, pageable);
        } else if (estado != null) {
            pagina = repository.findByEstado(estado, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public QuotaResponse obterPorId(Long id) {
        return mapper.toResponse(obterDoTenant(id));
    }

    @Transactional
    public QuotaResponse anular(Long id) {
        Quota quota = obterDoTenant(id);
        quota.setEstado(EstadoQuota.ANULADO);
        logger.info("Quota anulada: id={}", id);
        return mapper.toResponse(quota);
    }

    private Quota obterDoTenant(Long id) {
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
