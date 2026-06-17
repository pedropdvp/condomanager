package com.condomanager.service;

import com.condomanager.dto.DespesaDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Condominio;
import com.condomanager.model.Despesa;
import com.condomanager.model.enums.EstadoDespesa;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.DespesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DespesaService {

    private final DespesaRepository repository;
    private final CondominioRepository condominioRepository;

    public DespesaService(DespesaRepository repository, CondominioRepository condominioRepository) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
    }

    @Transactional(readOnly = true)
    public List<DespesaDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DespesaDTO obter(Long id) {
        return toDTO(buscar(id));
    }

    public DespesaDTO criar(DespesaDTO dto) {
        Despesa d = new Despesa();
        aplicar(dto, d);
        return toDTO(repository.save(d));
    }

    public DespesaDTO atualizar(Long id, DespesaDTO dto) {
        Despesa d = buscar(id);
        aplicar(dto, d);
        return toDTO(repository.save(d));
    }

    /** Aprova uma despesa (passa de PENDENTE a APROVADA). */
    public DespesaDTO aprovar(Long id) {
        Despesa d = buscar(id);
        d.setEstado(EstadoDespesa.APROVADA);
        return toDTO(repository.save(d));
    }

    public void apagar(Long id) {
        repository.delete(buscar(id));
    }

    private void aplicar(DespesaDTO dto, Despesa d) {
        Condominio c = condominioRepository.findById(dto.getCondominioId())
                .orElseThrow(() -> new ResourceNotFoundException("Condominio nao encontrado: " + dto.getCondominioId()));
        d.setDescricao(dto.getDescricao());
        d.setValor(dto.getValor());
        d.setData(dto.getData() != null ? dto.getData() : LocalDate.now());
        d.setCategoria(dto.getCategoria());
        if (dto.getEstado() != null) {
            d.setEstado(dto.getEstado());
        }
        d.setCondominio(c);
    }

    private Despesa buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despesa nao encontrada: " + id));
    }

    private DespesaDTO toDTO(Despesa d) {
        DespesaDTO dto = new DespesaDTO();
        dto.setId(d.getId());
        dto.setDescricao(d.getDescricao());
        dto.setValor(d.getValor());
        dto.setData(d.getData());
        dto.setCategoria(d.getCategoria());
        dto.setEstado(d.getEstado());
        dto.setCondominioId(d.getCondominio() != null ? d.getCondominio().getId() : null);
        return dto;
    }
}
