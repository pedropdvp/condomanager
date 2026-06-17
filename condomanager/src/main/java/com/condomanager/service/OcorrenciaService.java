package com.condomanager.service;

import com.condomanager.dto.OcorrenciaDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Condomino;
import com.condomanager.model.Ocorrencia;
import com.condomanager.model.enums.EstadoOcorrencia;
import com.condomanager.model.enums.Prioridade;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.OcorrenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final CondominoRepository condominoRepository;

    public OcorrenciaService(OcorrenciaRepository repository, CondominoRepository condominoRepository) {
        this.repository = repository;
        this.condominoRepository = condominoRepository;
    }

    @Transactional(readOnly = true)
    public List<OcorrenciaDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OcorrenciaDTO obter(Long id) {
        return toDTO(buscar(id));
    }

    /** Regista uma ocorrencia: avaria, reclamacao ou sugestao (RF14). */
    public OcorrenciaDTO registar(OcorrenciaDTO dto) {
        Ocorrencia o = new Ocorrencia();
        o.setTitulo(dto.getTitulo());
        o.setDescricao(dto.getDescricao());
        o.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoOcorrencia.ABERTA);
        o.setPrioridade(dto.getPrioridade() != null ? dto.getPrioridade() : Prioridade.MEDIA);
        o.setDataRegisto(LocalDateTime.now());
        if (dto.getCondominoId() != null) {
            Condomino c = condominoRepository.findById(dto.getCondominoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Condomino nao encontrado: " + dto.getCondominoId()));
            o.setCondomino(c);
        }
        return toDTO(repository.save(o));
    }

    /** Altera o estado da ocorrencia (em analise, resolvida, fechada). */
    public OcorrenciaDTO alterarEstado(Long id, EstadoOcorrencia estado) {
        Ocorrencia o = buscar(id);
        o.setEstado(estado);
        return toDTO(repository.save(o));
    }

    public void apagar(Long id) {
        repository.delete(buscar(id));
    }

    private Ocorrencia buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrencia nao encontrada: " + id));
    }

    private OcorrenciaDTO toDTO(Ocorrencia o) {
        OcorrenciaDTO dto = new OcorrenciaDTO();
        dto.setId(o.getId());
        dto.setTitulo(o.getTitulo());
        dto.setDescricao(o.getDescricao());
        dto.setEstado(o.getEstado());
        dto.setPrioridade(o.getPrioridade());
        dto.setDataRegisto(o.getDataRegisto());
        dto.setCondominoId(o.getCondomino() != null ? o.getCondomino().getId() : null);
        return dto;
    }
}
