package com.condomanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

/**
 * Superclasse para todas as entidades de negócio multi-tenant.
 *
 * <p>Acrescenta a coluna {@code id_empresa} (o discriminador de tenant) e define o
 * filtro {@code tenantFilter}, que é ativado por {@code TenantFilterAspect} para
 * impor {@code id_empresa = :tenantId} em todas as consultas — conforme
 * {@code docs/SAAS_RULES.md}.</p>
 *
 * <p>A entidade {@code EmpresaGestao} (raiz do tenant) <strong>não</strong> deve
 * estender esta classe, pois o seu próprio identificador é o tenant; usará apenas
 * {@link Auditable}.</p>
 */
@MappedSuperclass
@EntityListeners(TenantEntityListener.class)
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "id_empresa = :tenantId")
@Getter
@Setter
public abstract class TenantAwareEntity extends Auditable {

    @Column(name = "id_empresa", nullable = false, updatable = false)
    private Long idEmpresa;
}
