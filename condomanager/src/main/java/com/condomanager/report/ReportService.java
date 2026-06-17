package com.condomanager.report;

import com.condomanager.dto.RelatorioQuotaItem;
import com.condomanager.model.Quota;
import com.condomanager.model.enums.EstadoQuota;
import com.condomanager.repository.QuotaRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Geracao de relatorios em PDF com JasperReports (RF16 / Modulo 17).
 * Compila o template .jrxml, preenche com os dados e exporta para PDF.
 */
@Service
public class ReportService {

    private final QuotaRepository quotaRepository;
    private JasperReport relatorioQuotas;

    public ReportService(QuotaRepository quotaRepository) {
        this.quotaRepository = quotaRepository;
    }

    @Transactional(readOnly = true)
    public byte[] relatorioQuotasEmAtraso() {
        List<RelatorioQuotaItem> itens = quotaRepository.findByEstadoNot(EstadoQuota.PAGA).stream()
                .map(this::toItem)
                .collect(Collectors.toList());
        try {
            JasperReport jr = getRelatorioQuotas();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(itens);
            Map<String, Object> params = new HashMap<>();
            JasperPrint print = JasperFillManager.fillReport(jr, params, ds);
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException e) {
            throw new RuntimeException("Falha a gerar o relatorio de quotas: " + e.getMessage(), e);
        }
    }

    /** Compila o template uma vez e reutiliza-o (cache). */
    private synchronized JasperReport getRelatorioQuotas() throws JRException {
        if (relatorioQuotas == null) {
            try (InputStream is = getClass().getResourceAsStream("/reports/quotas_atraso.jrxml")) {
                if (is == null) {
                    throw new JRException("Template /reports/quotas_atraso.jrxml nao encontrado.");
                }
                relatorioQuotas = JasperCompileManager.compileReport(is);
            } catch (java.io.IOException e) {
                throw new JRException(e);
            }
        }
        return relatorioQuotas;
    }

    private RelatorioQuotaItem toItem(Quota q) {
        String condominio = q.getFracao() != null && q.getFracao().getCondominio() != null
                ? q.getFracao().getCondominio().getNome() : "-";
        String fracao = q.getFracao() != null ? q.getFracao().getNumero() : "-";
        String periodo = String.format("%02d/%d", q.getMes(), q.getAno());
        return new RelatorioQuotaItem(condominio, fracao, periodo, q.getValor(),
                q.getEstado() != null ? q.getEstado().name() : "-");
    }
}
