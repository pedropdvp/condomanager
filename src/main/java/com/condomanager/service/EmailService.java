package com.condomanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Envio de emails (recuperação de password, convocatórias, …).
 *
 * <p>Modos, por ordem de prioridade:</p>
 * <ol>
 *   <li>{@code app.mail.enabled=false} (default dev): o email é apenas registado no log.</li>
 *   <li>Se {@code app.mail.resend.api-key} estiver definido: envio via <strong>API HTTP da
 *       Resend</strong> (porta 443) — recomendado em PaaS que bloqueiam portas SMTP (ex.: Render free).</li>
 *   <li>Caso contrário: envio via <strong>SMTP</strong> ({@code JavaMailSender}).</li>
 * </ol>
 *
 * <p>Uma falha de envio é registada (ERROR) mas <strong>não propaga</strong>: um email que
 * não sai não deve quebrar o fluxo de negócio nem revelar a existência de contas.</p>
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String RESEND_URL = "https://api.resend.com/emails";

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String from;
    private final String resendApiKey;
    private final RestClient http = RestClient.create();

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.mail.enabled}") boolean enabled,
                        @Value("${app.mail.from}") String from,
                        @Value("${app.mail.resend.api-key:}") String resendApiKey) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
        this.resendApiKey = resendApiKey;
    }

    public void enviar(String para, String assunto, String corpo) {
        if (!enabled) {
            logger.info("[EMAIL-DEV] para={} | assunto='{}'\n{}", para, assunto, corpo);
            return;
        }
        try {
            if (resendApiKey != null && !resendApiKey.isBlank()) {
                enviarViaResend(para, assunto, corpo);
            } else {
                enviarViaSmtp(para, assunto, corpo);
            }
            logger.info("Email enviado para {} (assunto: {})", para, assunto);
        } catch (Exception e) {
            logger.error("Falha ao enviar email para {} (assunto: {}): {}", para, assunto, e.getMessage());
        }
    }

    private void enviarViaResend(String para, String assunto, String corpo) {
        http.post()
                .uri(RESEND_URL)
                .header("Authorization", "Bearer " + resendApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("from", from, "to", new String[]{para}, "subject", assunto, "text", corpo))
                .retrieve()
                .toBodilessEntity();
    }

    private void enviarViaSmtp(String para, String assunto, String corpo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(from);
        mensagem.setTo(para);
        mensagem.setSubject(assunto);
        mensagem.setText(corpo);
        mailSender.send(mensagem);
    }
}
