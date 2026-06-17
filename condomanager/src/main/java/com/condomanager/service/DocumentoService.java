package com.condomanager.service;

import com.condomanager.dto.DocumentoDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Condominio;
import com.condomanager.model.Documento;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.DocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentoService {

    private final DocumentoRepository repository;
    private final CondominioRepository condominioRepository;

    public DocumentoService(DocumentoRepository repository, CondominioRepository condominioRepository) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
    }

    @Transactional(readOnly = true)
    public List<DocumentoDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentoDTO obter(Long id) {
        return toDTO(buscar(id));
    }

    /** Regista (upload) um documento: regulamento, contrato, orcamento, ata, fatura, apolice (RF09). */
    public DocumentoDTO registar(DocumentoDTO dto) {
        Condominio c = condominioRepository.findById(dto.getCondominioId())
                .orElseThrow(() -> new ResourceNotFoundException("Condominio nao encontrado: " + dto.getCondominioId()));
        Documento d = new Documento();
        d.setNome(dto.getNome());
        d.setTipo(dto.getTipo());
        d.setFicheiro(dto.getFicheiro());
        d.setDataUpload(LocalDateTime.now());
        d.setCondominio(c);
        return toDTO(repository.save(d));
    }

    public void apagar(Long id) {
        repository.delete(buscar(id));
    }

    private Documento buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento nao encontrado: " + id));
    }

    private DocumentoDTO toDTO(Documento d) {
        DocumentoDTO dto = new DocumentoDTO();
        dto.setId(d.getId());
        dto.setNome(d.getNome());
        dto.setTipo(d.getTipo());
        dto.setFicheiro(d.getFicheiro());
        dto.setDataUpload(d.getDataUpload());
        dto.setCondominioId(d.getCondominio() != null ? d.getCondominio().getId() : null);
        return dto;
    }
}
