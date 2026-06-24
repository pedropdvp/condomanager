package com.condomanager.repository;

import com.condomanager.model.Edificio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EdificioRepository extends JpaRepository<Edificio, Long> {

    /** Acesso por id seguro entre tenants (o tenantFilter não cobre em.find). */
    Optional<Edificio> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    Page<Edificio> findByCondominio_Id(Long condominioId, Pageable pageable);
}
