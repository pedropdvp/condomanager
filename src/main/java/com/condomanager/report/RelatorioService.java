package com.condomanager.report;

import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.exception.StorageException;
import com.condomanager.model.Condominio;
import com.condomanager.model.Quota;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.QuotaRepository;
import com.condomanager.security.TenantContext;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Geração de relatórios em PDF com JasperReports (ver {@code docs/TECH_STACK.md}).
 */
@Service
public class RelatorioService {

    private static final String TEMPLATE_QUOTAS = "reports/relatorio_quotas.jrxml";

    private final CondominioRepository condominioRepository;
    private final QuotaRepository quotaRepository;

    /** Relatório compilado em cache (a compilação é cara). */
    private volatile JasperReport relatorioQuotasCompilado;

    public RelatorioService(CondominioRepository condominioRepository, QuotaRepository quotaRepository) {
        this.condominioRepository = condominioRepository;
        this.quotaRepository = quotaRepository;
    }

    @Transactional(readOnly = true)
    public byte[] relatorioQuotasPdf(Long condominioId) {
        JasperPrint print = preencherQuotas(condominioId);
        try {
            return JasperExportManager.exportReportToPdf(print);
        } catch (Exception e) {
            throw new StorageException("Falha ao gerar o relatório de quotas (PDF)", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] relatorioQuotasXlsx(Long condominioId) {
        JasperPrint print = preencherQuotas(condominioId);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            exporter.exportReport();
            return out.toByteArray();
        } catch (Exception e) {
            throw new StorageException("Falha ao gerar o relatório de quotas (Excel)", e);
        }
    }

    private JasperPrint preencherQuotas(Long condominioId) {
        Long tenant = tenantObrigatorio();
        Condominio condominio = condominioRepository.findByIdAndIdEmpresa(condominioId, tenant)
                .orElseThrow(() -> new ResourceNotFoundException("Condomínio", condominioId));

        List<QuotaReportRow> linhas = quotaRepository.findByFracao_Condominio_Id(condominioId).stream()
                .map(this::toRow)
                .toList();

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("CONDOMINIO_NOME", condominio.getNome());

        try {
            return JasperFillManager.fillReport(relatorioQuotas(), parametros, new JRBeanCollectionDataSource(linhas));
        } catch (Exception e) {
            throw new StorageException("Falha ao preencher o relatório de quotas", e);
        }
    }

    private QuotaReportRow toRow(Quota quota) {
        return new QuotaReportRow(
                "Fração " + quota.getFracao().getNumero(),
                quota.getMes() + "/" + quota.getAno(),
                quota.getValor(),
                quota.getEstado().name());
    }

    private JasperReport relatorioQuotas() throws Exception {
        JasperReport cache = relatorioQuotasCompilado;
        if (cache == null) {
            synchronized (this) {
                if (relatorioQuotasCompilado == null) {
                    try (InputStream in = new ClassPathResource(TEMPLATE_QUOTAS).getInputStream()) {
                        relatorioQuotasCompilado = JasperCompileManager.compileReport(in);
                    }
                }
                cache = relatorioQuotasCompilado;
            }
        }
        return cache;
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }
}
