package com.condomanager.service;

import com.condomanager.dto.ReuniaoDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Condominio;
import com.condomanager.model.Reuniao;
import com.condomanager.model.enums.EstadoReuniao;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.ReuniaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReuniaoService {

    private final ReuniaoRepository repository;
    private final CondominioRepository condominioRepository;

    public ReuniaoService(ReuniaoRepository repository, CondominioRepository condominioRepository) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
    }

    @Transactional(readOnly = true)
    public List<ReuniaoDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReuniaoDTO obter(Long id) {
        return toDTO(buscar(id));
    }

    public ReuniaoDTO agendar(ReuniaoDTO dto) {
        Reuniao r = new Reuniao();
        aplicar(dto, r);
        return toDTO(repository.save(r));
    }

    public ReuniaoDTO atualizar(Long id, ReuniaoDTO dto) {
        Reuniao r = buscar(id);
        aplicar(dto, r);
        return toDTO(repository.save(r));
    }

    public void apagar(Long id) {
        repository.delete(buscar(id));
    }

    private void aplicar(ReuniaoDTO dto, Reuniao r) {
        Condominio c = condominioRepository.findById(dto.getCondominioId())
                .orElseThrow(() -> new ResourceNotFoundException("Condominio nao encontrado: " + dto.getCondominioId()));
        r.setAssunto(dto.getAssunto());
        r.setData(dto.getData());
        r.setHora(dto.getHora());
        r.setLocal(dto.getLocal());
        r.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoReuniao.AGENDADA);
        r.setCondominio(c);
    }

    private Reuniao buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reuniao nao encontrada: " + id));
    }

    private ReuniaoDTO toDTO(Reuniao r) {
        ReuniaoDTO dto = new ReuniaoDTO();
        dto.setId(r.getId());
        dto.setAssunto(r.getAssunto());
        dto.setData(r.getData());
        dto.setHora(r.getHora());
        dto.setLocal(r.getLocal());
        dto.setEstado(r.getEstado());
        dto.setCondominioId(r.getCondominio() != null ? r.getCondominio().getId() : null);
        return dto;
    }
}
