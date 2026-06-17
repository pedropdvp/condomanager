package com.condomanager.controller;

import com.condomanager.report.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final ReportService reportService;

    public RelatorioController(ReportService reportService) {
        this.reportService = reportService;
    }

    /** Relatorio de pagamentos em atraso/pendentes em PDF (JasperReports). */
    @GetMapping("/quotas-atraso")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public ResponseEntity<byte[]> quotasEmAtraso() {
        byte[] pdf = reportService.relatorioQuotasEmAtraso();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=quotas_atraso.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
