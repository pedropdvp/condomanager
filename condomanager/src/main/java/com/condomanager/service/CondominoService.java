package com.condomanager.service;

import com.condomanager.dto.CondominoDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Condomino;
import com.condomanager.model.Fracao;
import com.condomanager.model.enums.TipoCondomino;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.FracaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CondominoService {

    private final CondominoRepository repository;
    private final FracaoRepository fracaoRepository;

    public CondominoService(CondominoRepository repository, FracaoRepository fracaoRepository) {
        this.repository = repository;
        this.fracaoRepository = fracaoRepository;
    }

    @Transactional(readOnly = true)
    public List<CondominoDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CondominoDTO obter(Long id) {
        return toDTO(buscar(id));
    }

    public CondominoDTO criar(CondominoDTO dto) {
        Condomino c = new Condomino();
        aplicar(dto, c);
        return toDTO(repository.save(c));
    }

    public CondominoDTO atualizar(Long id, CondominoDTO dto) {
        Condomino c = buscar(id);
        aplicar(dto, c);
        return toDTO(repository.save(c));
    }

    public void apagar(Long id) {
        repository.delete(buscar(id));
    }

    private void aplicar(CondominoDTO dto, Condomino c) {
        c.setNome(dto.getNome());
        c.setNif(dto.getNif());
        c.setEmail(dto.getEmail());
        c.setTelefone(dto.getTelefone());
        c.setTipo(dto.getTipo() != null ? dto.getTipo() : TipoCondomino.PROPRIETARIO);
        if (dto.getFracaoId() != null) {
            Fracao f = fracaoRepository.findById(dto.getFracaoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fracao nao encontrada: " + dto.getFracaoId()));
            c.setFracao(f);
        } else {
            c.setFracao(null);
        }
    }

    private Condomino buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Condomino nao encontrado: " + id));
    }

    private CondominoDTO toDTO(Condomino c) {
        CondominoDTO dto = new CondominoDTO();
        dto.setId(c.getId());
        dto.setNome(c.getNome());
        dto.setNif(c.getNif());
        dto.setEmail(c.getEmail());
        dto.setTelefone(c.getTelefone());
        dto.setTipo(c.getTipo());
        dto.setFracaoId(c.getFracao() != null ? c.getFracao().getId() : null);
        return dto;
    }
}
