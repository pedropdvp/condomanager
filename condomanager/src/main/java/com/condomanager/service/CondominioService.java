package com.condomanager.service;

import com.condomanager.dto.CondominioDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.CondominioMapper;
import com.condomanager.model.Condominio;
import com.condomanager.model.EmpresaGestao;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.EmpresaGestaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CondominioService {

    private final CondominioRepository repository;
    private final EmpresaGestaoRepository empresaRepository;
    private final CondominioMapper mapper;

    public CondominioService(CondominioRepository repository,
                             EmpresaGestaoRepository empresaRepository,
                             CondominioMapper mapper) {
        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CondominioDTO> listar() {
        return repository.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CondominioDTO obter(Long id) {
        return mapper.toDTO(buscar(id));
    }

    public CondominioDTO criar(CondominioDTO dto) {
        EmpresaGestao empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada: " + dto.getEmpresaId()));

        Condominio c = new Condominio();
        c.setNome(dto.getNome());
        c.setMorada(dto.getMorada());
        c.setOrcamentoAnual(dto.getOrcamentoAnual() != null ? dto.getOrcamentoAnual() : BigDecimal.ZERO);
        c.setEmpresa(empresa);
        return mapper.toDTO(repository.save(c));
    }

    public CondominioDTO atualizar(Long id, CondominioDTO dto) {
        Condominio c = buscar(id);
        c.setNome(dto.getNome());
        c.setMorada(dto.getMorada());
        if (dto.getOrcamentoAnual() != null) {
            c.setOrcamentoAnual(dto.getOrcamentoAnual());
        }
        return mapper.toDTO(repository.save(c));
    }

    public void apagar(Long id) {
        repository.delete(buscar(id));
    }

    private Condominio buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Condominio nao encontrado: " + id));
    }
}
