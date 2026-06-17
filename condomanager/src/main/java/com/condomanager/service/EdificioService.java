package com.condomanager.service;

import com.condomanager.dto.EdificioDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Condominio;
import com.condomanager.model.Edificio;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.EdificioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EdificioService {

    private final EdificioRepository repository;
    private final CondominioRepository condominioRepository;

    public EdificioService(EdificioRepository repository, CondominioRepository condominioRepository) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
    }

    @Transactional(readOnly = true)
    public List<EdificioDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EdificioDTO obter(Long id) {
        return toDTO(buscar(id));
    }

    public EdificioDTO criar(EdificioDTO dto) {
        Edificio e = new Edificio();
        aplicar(dto, e);
        return toDTO(repository.save(e));
    }

    public EdificioDTO atualizar(Long id, EdificioDTO dto) {
        Edificio e = buscar(id);
        aplicar(dto, e);
        return toDTO(repository.save(e));
    }

    public void apagar(Long id) {
        repository.delete(buscar(id));
    }

    private void aplicar(EdificioDTO dto, Edificio e) {
        Condominio c = condominioRepository.findById(dto.getCondominioId())
                .orElseThrow(() -> new ResourceNotFoundException("Condominio nao encontrado: " + dto.getCondominioId()));
        e.setNome(dto.getNome());
        e.setMorada(dto.getMorada());
        e.setNumPisos(dto.getNumPisos());
        e.setCondominio(c);
    }

    private Edificio buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Edificio nao encontrado: " + id));
    }

    private EdificioDTO toDTO(Edificio e) {
        EdificioDTO dto = new EdificioDTO();
        dto.setId(e.getId());
        dto.setNome(e.getNome());
        dto.setMorada(e.getMorada());
        dto.setNumPisos(e.getNumPisos());
        dto.setCondominioId(e.getCondominio() != null ? e.getCondominio().getId() : null);
        return dto;
    }
}
