package com.condomanager.configuration;

import com.condomanager.security.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Ativa automaticamente o filtro multi-tenant do Hibernate antes de qualquer
 * acesso a repositório.
 *
 * <p>O filtro {@code tenantFilter} (definido em {@code TenantAwareEntity}) acrescenta
 * a condição {@code id_empresa = :tenantId} a todas as consultas sobre entidades
 * multi-tenant, garantindo o isolamento entre empresas de gestão.</p>
 *
 * <p>Enquanto não existirem entidades que estendam {@code TenantAwareEntity}
 * (introduzidas a partir da Fase 2), o filtro não está registado e este aspeto
 * simplesmente não tem efeito — o {@code pointcut} sobre {@code repository} ainda
 * não corresponde a nada.</p>
 */
@Aspect
@Component
public class TenantFilterAspect {

    public static final String TENANT_FILTER = "tenantFilter";
    public static final String TENANT_PARAM = "tenantId";

    private static final Logger logger = LoggerFactory.getLogger(TenantFilterAspect.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.condomanager..repository..*(..))")
    public void enableTenantFilter() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return;
        }
        Session session = entityManager.unwrap(Session.class);
        if (session.getEnabledFilter(TENANT_FILTER) != null) {
            return;
        }
        try {
            Filter filter = session.enableFilter(TENANT_FILTER);
            filter.setParameter(TENANT_PARAM, tenantId);
        } catch (IllegalArgumentException ex) {
            // Filtro ainda não registado (sem entidades multi-tenant). Seguro ignorar nesta fase.
            logger.trace("Filtro multi-tenant ainda não registado: {}", ex.getMessage());
        }
    }
}
