package com.condomanager.service;

import com.condomanager.model.Historico;
import com.condomanager.repository.HistoricoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servico de auditoria (RF17 / RNF09). Persiste registos de operacoes no historico.
 */
@Service
public class AuditoriaService {

    private final HistoricoRepository repository;

    public AuditoriaService(HistoricoRepository repository) {
        this.repository = repository;
    }

    /** Grava numa transacao propria para nao depender da transacao da operacao auditada. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void gravar(String utilizador, String operacao) {
        repository.save(new Historico(utilizador, operacao));
    }

    @Transactional(readOnly = true)
    public List<Historico> listar() {
        return repository.findAll();
    }
}
