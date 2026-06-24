package com.condomanager.security;

/**
 * Guarda o identificador do tenant (empresa de gestão) associado ao pedido atual.
 *
 * <p>O valor é mantido num {@link ThreadLocal}, sendo preenchido no início de cada
 * pedido HTTP (ver {@code TenantFilter}) e limpo no fim, para garantir o isolamento
 * multi-tenant exigido por {@code docs/SAAS_RULES.md}.</p>
 */
public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
        // Classe utilitária — não instanciável.
    }

    public static void setTenantId(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
