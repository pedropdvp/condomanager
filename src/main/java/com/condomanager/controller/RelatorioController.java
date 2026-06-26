package com.condomanager.controller;

import com.condomanager.report.RelatorioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Relatórios em PDF (JasperReports), no âmbito do tenant.
 */
@RestController
@RequestMapping("/api/v1/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping("/quotas")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<byte[]> quotas(@RequestParam Long condominioId) {
        byte[] pdf = service.relatorioQuotasPdf(condominioId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"relatorio-quotas-" + condominioId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/quotas/excel")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<byte[]> quotasExcel(@RequestParam Long condominioId) {
        return xlsx(service.relatorioQuotasXlsx(condominioId), "quotas", condominioId);
    }

    @GetMapping("/despesas")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<byte[]> despesas(@RequestParam Long condominioId) {
        return pdf(service.relatorioDespesasPdf(condominioId), "despesas", condominioId);
    }

    @GetMapping("/despesas/excel")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<byte[]> despesasExcel(@RequestParam Long condominioId) {
        return xlsx(service.relatorioDespesasXlsx(condominioId), "despesas", condominioId);
    }

    @GetMapping("/ocorrencias")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<byte[]> ocorrencias(@RequestParam Long condominioId) {
        return pdf(service.relatorioOcorrenciasPdf(condominioId), "ocorrencias", condominioId);
    }

    @GetMapping("/ocorrencias/excel")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<byte[]> ocorrenciasExcel(@RequestParam Long condominioId) {
        return xlsx(service.relatorioOcorrenciasXlsx(condominioId), "ocorrencias", condominioId);
    }

    private ResponseEntity<byte[]> pdf(byte[] body, String nome, Long condominioId) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"relatorio-" + nome + "-" + condominioId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(body);
    }

    private ResponseEntity<byte[]> xlsx(byte[] body, String nome, Long condominioId) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"relatorio-" + nome + "-" + condominioId + ".xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(body);
    }
}
