package com.condomanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Superclasse para campos de auditoria temporal.
 *
 * <p>Cumpre a regra de {@code docs/DATABASE_SCHEMA.md}: todas as tabelas devem ter
 * {@code created_at} e {@code updated_at}. Os campos opcionais {@code created_by} /
 * {@code updated_by} serão acrescentados na Fase 1 (Autenticação), quando existir um
 * utilizador autenticado para os preencher.</p>
 */
@MappedSuperclass
@Getter
@Setter
public abstract class Auditable {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
