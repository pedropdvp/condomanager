package com.condomanager.service;

import com.condomanager.dto.EmpresaDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.EmpresaMapper;
import com.condomanager.model.EmpresaGestao;
import com.condomanager.repository.EmpresaGestaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmpresaService {

    private final EmpresaGestaoRepository repository;
    private final EmpresaMapper mapper;

    public EmpresaService(EmpresaGestaoRepository repository, EmpresaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<EmpresaDTO> listar() {
        return repository.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpresaDTO obter(Long id) {
        return mapper.toDTO(buscar(id));
    }

    public EmpresaDTO criar(EmpresaDTO dto) {
        if (repository.existsByNif(dto.getNif())) {
            throw new IllegalArgumentException("Ja existe uma empresa com o NIF " + dto.getNif());
        }
        EmpresaGestao guardada = repository.save(mapper.toEntity(dto));
        return mapper.toDTO(guardada);
    }

    public EmpresaDTO atualizar(Long id, EmpresaDTO dto) {
        EmpresaGestao e = buscar(id);
        mapper.aplicar(dto, e);
        return mapper.toDTO(repository.save(e));
    }

    public void apagar(Long id) {
        EmpresaGestao e = buscar(id);
        repository.delete(e);
    }

    private EmpresaGestao buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada: " + id));
    }
}
