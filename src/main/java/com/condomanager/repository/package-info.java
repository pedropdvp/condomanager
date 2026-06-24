/**
 * Camada de acesso a dados: interfaces Spring Data JPA.
 *
 * <p>Todas as consultas sobre entidades multi-tenant são automaticamente filtradas
 * por {@code id_empresa} através do filtro do Hibernate ativado em
 * {@code com.condomanager.configuration.TenantFilterAspect}.</p>
 */
package com.condomanager.repository;
