package com.condomanager.repository;

import com.condomanager.model.Historico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoRepository extends JpaRepository<Historico, Long> {

    Page<Historico> findByIdEmpresaOrderByDataHoraDesc(Long idEmpresa, Pageable pageable);

    Page<Historico> findAllByOrderByDataHoraDesc(Pageable pageable);
}
