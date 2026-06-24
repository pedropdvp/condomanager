package com.condomanager.repository;

import com.condomanager.model.Condominio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CondominioRepository extends JpaRepository<Condominio, Long> {

    /**
     * Procura um condomínio garantindo que pertence ao tenant indicado.
     *
     * <p>Necessário porque o filtro {@code tenantFilter} do Hibernate <em>não</em> é
     * aplicado em carregamentos por chave primária ({@code em.find}); ao filtrar
     * explicitamente por {@code id_empresa} evita-se o acesso entre tenants.</p>
     */
    Optional<Condominio> findByIdAndIdEmpresa(Long id, Long idEmpresa);
}
