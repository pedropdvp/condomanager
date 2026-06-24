package com.condomanager.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários do {@link TenantContext} (sem necessidade de base de dados).
 */
class TenantContextTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void deveGuardarEDevolverOTenant() {
        TenantContext.setTenantId(42L);

        assertThat(TenantContext.getTenantId()).isEqualTo(42L);
        assertThat(TenantContext.hasTenant()).isTrue();
    }

    @Test
    void deveComecarSemTenant() {
        assertThat(TenantContext.getTenantId()).isNull();
        assertThat(TenantContext.hasTenant()).isFalse();
    }

    @Test
    void deveLimparOTenant() {
        TenantContext.setTenantId(7L);

        TenantContext.clear();

        assertThat(TenantContext.getTenantId()).isNull();
        assertThat(TenantContext.hasTenant()).isFalse();
    }
}
