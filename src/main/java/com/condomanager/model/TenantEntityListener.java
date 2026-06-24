package com.condomanager.model;

import com.condomanager.security.TenantContext;
import jakarta.persistence.PrePersist;

/**
 * Preenche automaticamente o {@code id_empresa} de uma entidade multi-tenant, a partir
 * do {@link TenantContext}, no momento da persistência.
 *
 * <p>Evita que cada serviço tenha de definir o tenant manualmente e reduz o risco de
 * gravar registos sem isolamento.</p>
 */
public class TenantEntityListener {

    @PrePersist
    public void applyTenant(Object entity) {
        if (entity instanceof TenantAwareEntity tenantAware && tenantAware.getIdEmpresa() == null) {
            Long tenantId = TenantContext.getTenantId();
            if (tenantId != null) {
                tenantAware.setIdEmpresa(tenantId);
            }
        }
    }
}
