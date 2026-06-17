package com.condomanager.configuration;

import com.condomanager.service.AuditoriaService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspecto de auditoria: regista automaticamente as operacoes de escrita
 * (criar, atualizar, apagar, registar, etc.) executadas na camada de servico.
 */
@Aspect
@Component
public class AuditAspect {

    private final AuditoriaService auditoriaService;

    public AuditAspect(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @AfterReturning(
            "execution(* com.condomanager.service..*.criar*(..)) || " +
            "execution(* com.condomanager.service..*.atualizar*(..)) || " +
            "execution(* com.condomanager.service..*.apagar*(..)) || " +
            "execution(* com.condomanager.service..*.registar*(..)) || " +
            "execution(* com.condomanager.service..*.enviar*(..)) || " +
            "execution(* com.condomanager.service..*.agendar*(..)) || " +
            "execution(* com.condomanager.service..*.votar*(..)) || " +
            "execution(* com.condomanager.service..*.arquivar*(..)) || " +
            "execution(* com.condomanager.service..*.encerrar*(..)) || " +
            "execution(* com.condomanager.service..*.aprovar*(..)) || " +
            "execution(* com.condomanager.service..*.alterarEstado*(..))")
    public void registarOperacao(JoinPoint jp) {
        String utilizador = utilizadorAtual();
        String operacao = jp.getSignature().getDeclaringType().getSimpleName()
                + "." + jp.getSignature().getName();
        auditoriaService.gravar(utilizador, operacao);
    }

    private String utilizadorAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "SISTEMA";
        }
        return auth.getName();
    }
}
