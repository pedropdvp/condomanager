package com.condomanager.service;

import com.condomanager.model.Condominio;
import com.condomanager.model.Fracao;
import com.condomanager.model.Quota;
import com.condomanager.model.enums.EstadoQuota;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.FracaoRepository;
import com.condomanager.repository.QuotaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Motor de faturacao automatica (Modulo 8 do planeamento).
 * No dia 1 de cada mes gera as quotas mensais de cada fracao, calculadas a partir
 * do orcamento anual do condominio e da permilagem da fracao.
 *
 * quota_mensal = (orcamento_anual / 12) * (permilagem / 1000)
 */
@Service
public class QuotaScheduler {

    private static final Logger log = LoggerFactory.getLogger(QuotaScheduler.class);
    private static final BigDecimal MIL = new BigDecimal("1000");
    private static final BigDecimal DOZE = new BigDecimal("12");

    private final CondominioRepository condominioRepository;
    private final FracaoRepository fracaoRepository;
    private final QuotaRepository quotaRepository;

    public QuotaScheduler(CondominioRepository condominioRepository,
                          FracaoRepository fracaoRepository,
                          QuotaRepository quotaRepository) {
        this.condominioRepository = condominioRepository;
        this.fracaoRepository = fracaoRepository;
        this.quotaRepository = quotaRepository;
    }

    /** Executa todo o dia 1 de cada mes as 02:00. */
    @Scheduled(cron = "0 0 2 1 * *")
    @Transactional
    public void gerarQuotasMensais() {
        LocalDate hoje = LocalDate.now();
        int mes = hoje.getMonthValue();
        int ano = hoje.getYear();
        log.info("Faturacao automatica: a gerar quotas para {}/{}", mes, ano);

        int geradas = 0;
        for (Condominio condominio : condominioRepository.findAll()) {
            List<Fracao> fracoes = fracaoRepository.findByCondominioId(condominio.getId());
            for (Fracao fracao : fracoes) {
                if (quotaRepository.existsByFracaoIdAndMesAndAno(fracao.getId(), mes, ano)) {
                    continue;
                }
                BigDecimal valor = calcularQuota(condominio.getOrcamentoAnual(), fracao.getPermilagem());
                Quota quota = new Quota();
                quota.setMes(mes);
                quota.setAno(ano);
                quota.setValor(valor);
                quota.setEstado(EstadoQuota.PENDENTE);
                quota.setFracao(fracao);
                quotaRepository.save(quota);
                geradas++;
            }
        }
        log.info("Faturacao automatica concluida: {} quotas geradas.", geradas);
    }

    private BigDecimal calcularQuota(BigDecimal orcamentoAnual, BigDecimal permilagem) {
        if (orcamentoAnual == null || permilagem == null) {
            return BigDecimal.ZERO;
        }
        return orcamentoAnual
                .divide(DOZE, 4, RoundingMode.HALF_UP)
                .multiply(permilagem.divide(MIL, 6, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
