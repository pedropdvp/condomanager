package com.condomanager.service;

import com.condomanager.dto.DashboardResponse;
import com.condomanager.model.EstadoOcorrencia;
import com.condomanager.model.EstadoQuota;
import com.condomanager.model.EstadoReuniao;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.DespesaRepository;
import com.condomanager.repository.EdificioRepository;
import com.condomanager.repository.FracaoRepository;
import com.condomanager.repository.OcorrenciaRepository;
import com.condomanager.repository.QuotaRepository;
import com.condomanager.repository.ReuniaoRepository;
import com.condomanager.repository.UtilizadorRepository;
import com.condomanager.security.TenantContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

/**
 * Agrega indicadores da empresa (tenant) para o dashboard.
 *
 * <p>Todas as contagens/somas sobre entidades multi-tenant são automaticamente
 * limitadas à empresa atual pelo filtro do Hibernate.</p>
 */
@Service
public class DashboardService {

    private final CondominioRepository condominioRepository;
    private final EdificioRepository edificioRepository;
    private final FracaoRepository fracaoRepository;
    private final CondominoRepository condominoRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final QuotaRepository quotaRepository;
    private final DespesaRepository despesaRepository;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final ReuniaoRepository reuniaoRepository;

    public DashboardService(CondominioRepository condominioRepository,
                            EdificioRepository edificioRepository,
                            FracaoRepository fracaoRepository,
                            CondominoRepository condominoRepository,
                            UtilizadorRepository utilizadorRepository,
                            QuotaRepository quotaRepository,
                            DespesaRepository despesaRepository,
                            OcorrenciaRepository ocorrenciaRepository,
                            ReuniaoRepository reuniaoRepository) {
        this.condominioRepository = condominioRepository;
        this.edificioRepository = edificioRepository;
        this.fracaoRepository = fracaoRepository;
        this.condominoRepository = condominoRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.quotaRepository = quotaRepository;
        this.despesaRepository = despesaRepository;
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.reuniaoRepository = reuniaoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse overview() {
        Long tenant = tenantObrigatorio();

        DashboardResponse.Estrutura estrutura = new DashboardResponse.Estrutura(
                condominioRepository.count(),
                edificioRepository.count(),
                fracaoRepository.count(),
                condominoRepository.count(),
                utilizadorRepository.countByIdEmpresa(tenant)
        );

        Map<EstadoQuota, Long> quotasContagem = new EnumMap<>(EstadoQuota.class);
        Map<EstadoQuota, BigDecimal> quotasValor = new EnumMap<>(EstadoQuota.class);
        for (EstadoQuota estado : EstadoQuota.values()) {
            quotasContagem.put(estado, quotaRepository.countByEstado(estado));
            quotasValor.put(estado, quotaRepository.somaValorPorEstado(estado));
        }
        DashboardResponse.Financeiro financeiro = new DashboardResponse.Financeiro(
                quotasContagem, quotasValor, despesaRepository.somaTotal());

        Map<EstadoOcorrencia, Long> ocorrencias = new EnumMap<>(EstadoOcorrencia.class);
        for (EstadoOcorrencia estado : EstadoOcorrencia.values()) {
            ocorrencias.put(estado, ocorrenciaRepository.countByEstado(estado));
        }

        long reunioesAgendadas = reuniaoRepository.countByEstado(EstadoReuniao.AGENDADA);

        return new DashboardResponse(estrutura, financeiro, ocorrencias, reunioesAgendadas);
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("O dashboard requer uma empresa associada.");
        }
        return tenant;
    }
}
