package com.condomanager.repository;

import com.condomanager.model.Mensagem;
import com.condomanager.model.TipoMensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    Optional<Mensagem> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    Page<Mensagem> findByOrigem_Id(Long origemId, Pageable pageable);

    /**
     * Caixa de entrada do utilizador: mensagens individuais para ele mais todas as
     * mensagens broadcast (a filtragem por empresa é garantida pelo tenantFilter).
     */
    @Query("""
            select m from Mensagem m
            where m.destino.id = :utilizadorId
               or m.tipo = :broadcast
               or :utilizadorId member of m.destinatarios
            """)
    Page<Mensagem> caixaDeEntrada(@Param("utilizadorId") Long utilizadorId,
                                  @Param("broadcast") TipoMensagem broadcast,
                                  Pageable pageable);

    long countByDestino_IdAndLidaFalse(Long destinoId);
}
