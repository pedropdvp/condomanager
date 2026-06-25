package com.condomanager.repository;

import com.condomanager.model.Acao;
import com.condomanager.model.Funcionalidade;
import com.condomanager.model.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {

    /** Existe permissão para algum dos perfis indicados sobre (funcionalidade, ação)? */
    boolean existsByPerfil_NomeInAndFuncionalidadeAndAcao(
            Collection<String> perfis, Funcionalidade funcionalidade, Acao acao);

    List<Permissao> findByPerfil_Nome(String nome);

    List<Permissao> findByPerfil_NomeIn(Collection<String> nomes);

    @Modifying
    @Transactional
    void deleteByPerfil_Nome(String nome);
}
