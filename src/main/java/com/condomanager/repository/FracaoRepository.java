package com.condomanager.repository;

import com.condomanager.model.Fracao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FracaoRepository extends JpaRepository<Fracao, Long> {

    Optional<Fracao> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    Page<Fracao> findByCondominio_Id(Long condominioId, Pageable pageable);

    Page<Fracao> findByEdificio_Id(Long edificioId, Pageable pageable);

    /** Todas as frações de um condomínio (usado na geração de quotas). */
    List<Fracao> findByCondominio_Id(Long condominioId);
}
