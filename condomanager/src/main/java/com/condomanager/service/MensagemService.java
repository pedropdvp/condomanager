package com.condomanager.service;

import com.condomanager.dto.MensagemDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Condominio;
import com.condomanager.model.Condomino;
import com.condomanager.model.Mensagem;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.MensagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MensagemService {

    private final MensagemRepository repository;
    private final CondominioRepository condominioRepository;
    private final CondominoRepository condominoRepository;

    public MensagemService(MensagemRepository repository,
                           CondominioRepository condominioRepository,
                           CondominoRepository condominoRepository) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
        this.condominoRepository = condominoRepository;
    }

    @Transactional(readOnly = true)
    public List<MensagemDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MensagemDTO obter(Long id) {
        return toDTO(buscar(id));
    }

    /** Envia mensagem a um condomino, a um grupo ou a todos (RF13). */
    public MensagemDTO enviar(MensagemDTO dto) {
        Condominio c = condominioRepository.findById(dto.getCondominioId())
                .orElseThrow(() -> new ResourceNotFoundException("Condominio nao encontrado: " + dto.getCondominioId()));
        Mensagem m = new Mensagem();
        m.setAssunto(dto.getAssunto());
        m.setConteudo(dto.getConteudo());
        m.setDataEnvio(LocalDateTime.now());
        m.setDestino(dto.getDestino() != null ? dto.getDestino() : "TODOS");
        m.setCondominio(c);
        if (dto.getCondominoId() != null) {
            Condomino destinatario = condominoRepository.findById(dto.getCondominoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Condomino nao encontrado: " + dto.getCondominoId()));
            m.setCondomino(destinatario);
            m.setDestino("CONDOMINO");
        }
        return toDTO(repository.save(m));
    }

    private Mensagem buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem nao encontrada: " + id));
    }

    private MensagemDTO toDTO(Mensagem m) {
        MensagemDTO dto = new MensagemDTO();
        dto.setId(m.getId());
        dto.setAssunto(m.getAssunto());
        dto.setConteudo(m.getConteudo());
        dto.setDataEnvio(m.getDataEnvio());
        dto.setDestino(m.getDestino());
        dto.setCondominoId(m.getCondomino() != null ? m.getCondomino().getId() : null);
        dto.setCondominioId(m.getCondominio() != null ? m.getCondominio().getId() : null);
        return dto;
    }
}
