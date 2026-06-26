package com.condomanager.report;

import com.condomanager.dto.BalanceteResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.exception.StorageException;
import com.condomanager.model.Condominio;
import com.condomanager.model.Despesa;
import com.condomanager.model.EstadoDespesa;
import com.condomanager.model.EstadoQuota;
import com.condomanager.model.Ocorrencia;
import com.condomanager.model.Quota;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.DespesaRepository;
import com.condomanager.repository.OcorrenciaRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Geração de relatórios em PDF/Excel com JasperReports (ver {@code docs/TECH_STACK.md}).
 *
 * <p>O relatório de quotas usa um template dedicado; os de Despesas e Ocorrências
 * reutilizam um template tabular <strong>genérico</strong> de 5 colunas.</p>
 */
@Service
public class RelatorioService {

    private static final String TEMPLATE_QUOTAS = "reports/relatorio_quotas.jrxml";
    private static final String TEMPLATE_GENERICO = "reports/relatorio_generico.jrxml";

    private final CondominioRepository condominioRepository;
    private final QuotaRepository quotaRepository;
    private final DespesaRepository despesaRepository;
    private final OcorrenciaRepository ocorrenciaRepository;

    /** Relatórios compilados em cache (a compilação é cara). */
    private volatile JasperReport relatorioQuotasCompilado;
    private volatile JasperReport relatorioGenericoCompilado;

    public RelatorioService(CondominioRepository condominioRepository,
                            QuotaRepository quotaRepository,
                            DespesaRepository despesaRepository,
                            OcorrenciaRepository ocorrenciaRepository) {
        this.condominioRepository = condominioRepository;
        this.quotaRepository = quotaRepository;
        this.despesaRepository = despesaRepository;
        this.ocorrenciaRepository = ocorrenciaRepository;
    }

    // ---------- Quotas (template dedicado) ----------

    @Transactional(readOnly = true)
    public byte[] relatorioQuotasPdf(Long condominioId) {
        return toPdf(preencherQuotas(condominioId), "quotas");
    }

    @Transactional(readOnly = true)
    public byte[] relatorioQuotasXlsx(Long condominioId) {
        return toXlsx(preencherQuotas(condominioId), "quotas");
    }

    private JasperPrint preencherQuotas(Long condominioId) {
        Condominio condominio = condominioDoTenant(condominioId);
        List<QuotaReportRow> linhas = quotaRepository.findByFracao_Condominio_Id(condominioId).stream()
                .map(this::toRow)
                .toList();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("CONDOMINIO_NOME", condominio.getNome());
        try {
            return JasperFillManager.fillReport(compilar(TEMPLATE_QUOTAS, true), parametros,
                    new JRBeanCollectionDataSource(linhas));
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

    // ---------- Despesas (template genérico) ----------

    @Transactional(readOnly = true)
    public byte[] relatorioDespesasPdf(Long condominioId) {
        return toPdf(preencherDespesas(condominioId), "despesas");
    }

    @Transactional(readOnly = true)
    public byte[] relatorioDespesasXlsx(Long condominioId) {
        return toXlsx(preencherDespesas(condominioId), "despesas");
    }

    private JasperPrint preencherDespesas(Long condominioId) {
        Condominio condominio = condominioDoTenant(condominioId);
        List<RelatorioGenericoRow> linhas = despesaRepository
                .findByCondominio_Id(condominioId, Pageable.unpaged()).getContent().stream()
                .map(this::toRow)
                .toList();
        return preencherGenerico("Relatório de Despesas — " + condominio.getNome(),
                new String[]{"Data", "Descrição", "Categoria", "Valor (€)", "Estado"}, linhas);
    }

    private RelatorioGenericoRow toRow(Despesa d) {
        return new RelatorioGenericoRow(
                String.valueOf(d.getDataDespesa()),
                nz(d.getDescricao()),
                d.getCategoria() != null ? d.getCategoria().name() : "",
                d.getValor() != null ? d.getValor().toPlainString() : "",
                d.getEstado() != null ? d.getEstado().name() : "");
    }

    // ---------- Ocorrências (template genérico) ----------

    @Transactional(readOnly = true)
    public byte[] relatorioOcorrenciasPdf(Long condominioId) {
        return toPdf(preencherOcorrencias(condominioId), "ocorrencias");
    }

    @Transactional(readOnly = true)
    public byte[] relatorioOcorrenciasXlsx(Long condominioId) {
        return toXlsx(preencherOcorrencias(condominioId), "ocorrencias");
    }

    private JasperPrint preencherOcorrencias(Long condominioId) {
        Condominio condominio = condominioDoTenant(condominioId);
        List<RelatorioGenericoRow> linhas = ocorrenciaRepository
                .findByCondominio_Id(condominioId, Pageable.unpaged()).getContent().stream()
                .map(this::toRow)
                .toList();
        return preencherGenerico("Relatório de Ocorrências — " + condominio.getNome(),
                new String[]{"Data", "Título", "Prioridade", "Estado", "Descrição"}, linhas);
    }

    private RelatorioGenericoRow toRow(Ocorrencia o) {
        return new RelatorioGenericoRow(
                o.getCreatedAt() != null ? o.getCreatedAt().toLocalDate().toString() : "",
                nz(o.getTitulo()),
                o.getPrioridade() != null ? o.getPrioridade().name() : "",
                o.getEstado() != null ? o.getEstado().name() : "",
                nz(o.getDescricao()));
    }

    // ---------- Balancete / Tesouraria ----------

    @Transactional(readOnly = true)
    public BalanceteResponse balancete(Long condominioId) {
        Condominio c = condominioDoTenant(condominioId);
        List<Quota> quotas = quotaRepository.findByFracao_Condominio_Id(condominioId);
        BigDecimal totalQuotas = somaQuotas(quotas, null);
        BigDecimal pagas = somaQuotas(quotas, EstadoQuota.PAGO);
        BigDecimal porCobrar = somaQuotas(quotas, EstadoQuota.PENDENTE).add(somaQuotas(quotas, EstadoQuota.ATRASADO));
        List<Despesa> despesas = despesaRepository.findByCondominio_Id(condominioId, Pageable.unpaged()).getContent();
        BigDecimal despAprov = somaDespesas(despesas, EstadoDespesa.APROVADA);
        BigDecimal despPend = somaDespesas(despesas, EstadoDespesa.PENDENTE);
        BigDecimal saldo = pagas.subtract(despAprov);
        BigDecimal fundoReserva = totalQuotas.multiply(new BigDecimal("0.10")).setScale(2, java.math.RoundingMode.HALF_UP);
        return new BalanceteResponse(condominioId, c.getNome(), c.getOrcamentoAnual(),
                totalQuotas, pagas, porCobrar, despAprov, despPend, saldo, fundoReserva);
    }

    @Transactional(readOnly = true)
    public byte[] balancetePdf(Long condominioId) {
        BalanceteResponse b = balancete(condominioId);
        List<RelatorioGenericoRow> linhas = List.of(
                linhaBalancete("Orçamento anual", b.orcamentoAnual()),
                linhaBalancete("Receitas — quotas pagas", b.quotasPagas()),
                linhaBalancete("Quotas por cobrar (pendentes/atraso)", b.quotasPorCobrar()),
                linhaBalancete("Despesas aprovadas", b.despesasAprovadas()),
                linhaBalancete("Despesas pendentes", b.despesasPendentes()),
                linhaBalancete("SALDO (pagas - despesas aprovadas)", b.saldo()),
                linhaBalancete("Fundo de reserva (>=10% das quotas)", b.fundoReserva()));
        return toPdf(preencherGenerico("Balancete — " + b.condominioNome(),
                new String[]{"Rubrica", "Valor (€)", "", "", ""}, linhas), "balancete");
    }

    private RelatorioGenericoRow linhaBalancete(String rubrica, BigDecimal valor) {
        return new RelatorioGenericoRow(rubrica, valor != null ? valor.toPlainString() : "0", "", "", "");
    }

    private BigDecimal somaQuotas(List<Quota> quotas, EstadoQuota estado) {
        return quotas.stream().filter(q -> estado == null || q.getEstado() == estado)
                .map(Quota::getValor).filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal somaDespesas(List<Despesa> despesas, EstadoDespesa estado) {
        return despesas.stream().filter(d -> estado == null || d.getEstado() == estado)
                .map(Despesa::getValor).filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ---------- Infraestrutura comum ----------

    private JasperPrint preencherGenerico(String titulo, String[] colunas, List<RelatorioGenericoRow> linhas) {
        Map<String, Object> p = new HashMap<>();
        p.put("TITULO", titulo);
        for (int i = 0; i < 5; i++) {
            p.put("COL" + (i + 1), i < colunas.length ? colunas[i] : "");
        }
        try {
            return JasperFillManager.fillReport(compilar(TEMPLATE_GENERICO, false), p,
                    new JRBeanCollectionDataSource(linhas));
        } catch (Exception e) {
            throw new StorageException("Falha ao preencher o relatório", e);
        }
    }

    private byte[] toPdf(JasperPrint print, String nome) {
        try {
            return JasperExportManager.exportReportToPdf(print);
        } catch (Exception e) {
            throw new StorageException("Falha ao gerar o relatório de " + nome + " (PDF)", e);
        }
    }

    private byte[] toXlsx(JasperPrint print, String nome) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            exporter.exportReport();
            return out.toByteArray();
        } catch (Exception e) {
            throw new StorageException("Falha ao gerar o relatório de " + nome + " (Excel)", e);
        }
    }

    /** Compila (e cacheia) o template indicado. */
    private JasperReport compilar(String template, boolean quotas) throws Exception {
        JasperReport cache = quotas ? relatorioQuotasCompilado : relatorioGenericoCompilado;
        if (cache == null) {
            synchronized (this) {
                JasperReport atual = quotas ? relatorioQuotasCompilado : relatorioGenericoCompilado;
                if (atual == null) {
                    try (InputStream in = new ClassPathResource(template).getInputStream()) {
                        atual = JasperCompileManager.compileReport(in);
                    }
                    if (quotas) {
                        relatorioQuotasCompilado = atual;
                    } else {
                        relatorioGenericoCompilado = atual;
                    }
                }
                cache = atual;
            }
        }
        return cache;
    }

    private Condominio condominioDoTenant(Long condominioId) {
        return condominioRepository.findByIdAndIdEmpresa(condominioId, tenantObrigatorio())
                .orElseThrow(() -> new ResourceNotFoundException("Condomínio", condominioId));
    }

    private static String nz(String s) {
        return s != null ? s : "";
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }
}
