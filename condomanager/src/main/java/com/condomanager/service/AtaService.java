package com.condomanager.service;

import com.condomanager.dto.AtaDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Ata;
import com.condomanager.model.Condominio;
import com.condomanager.repository.AtaRepository;
import com.condomanager.repository.CondominioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AtaService {

    private final AtaRepository repository;
    private final CondominioRepository condominioRepository;

    public AtaService(AtaRepository repository, CondominioRepository condominioRepository) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
    }

    @Transactional(readOnly = true)
    public List<AtaDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AtaDTO obter(Long id) {
        return toDTO(buscar(id));
    }

    public AtaDTO criar(AtaDTO dto) {
        Ata a = new Ata();
        aplicar(dto, a);
        return toDTO(repository.save(a));
    }

    public AtaDTO atualizar(Long id, AtaDTO dto) {
        Ata a = buscar(id);
        aplicar(dto, a);
        return toDTO(repository.save(a));
    }

    /** Arquiva uma ata (RF08). */
    public AtaDTO arquivar(Long id) {
        Ata a = buscar(id);
        a.setArquivada(true);
        return toDTO(repository.save(a));
    }

    public void apagar(Long id) {
        repository.delete(buscar(id));
    }

    private void aplicar(AtaDTO dto, Ata a) {
        Condominio c = condominioRepository.findById(dto.getCondominioId())
                .orElseThrow(() -> new ResourceNotFoundException("Condominio nao encontrado: " + dto.getCondominioId()));
        a.setTitulo(dto.getTitulo());
        a.setDescricao(dto.getDescricao());
        a.setDataReuniao(dto.getDataReuniao());
        a.setFicheiro(dto.getFicheiro());
        a.setArquivada(dto.isArquivada());
        a.setCondominio(c);
    }

    private Ata buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ata nao encontrada: " + id));
    }

    private AtaDTO toDTO(Ata a) {
        AtaDTO dto = new AtaDTO();
        dto.setId(a.getId());
        dto.setTitulo(a.getTitulo());
        dto.setDescricao(a.getDescricao());
        dto.setDataReuniao(a.getDataReuniao());
        dto.setFicheiro(a.getFicheiro());
        dto.setArquivada(a.isArquivada());
        dto.setCondominioId(a.getCondominio() != null ? a.getCondominio().getId() : null);
        return dto;
    }
}
