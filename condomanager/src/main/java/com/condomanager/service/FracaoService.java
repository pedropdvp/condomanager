package com.condomanager.service;

import com.condomanager.dto.FracaoDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Condominio;
import com.condomanager.model.Edificio;
import com.condomanager.model.Fracao;
import com.condomanager.model.enums.Plano;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.EdificioRepository;
import com.condomanager.repository.FracaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FracaoService {

    private static final BigDecimal LIMITE_PERMILAGEM = new BigDecimal("1000");

    private final FracaoRepository repository;
    private final CondominioRepository condominioRepository;
    private final EdificioRepository edificioRepository;

    public FracaoService(FracaoRepository repository,
                         CondominioRepository condominioRepository,
                         EdificioRepository edificioRepository) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
        this.edificioRepository = edificioRepository;
    }

    @Transactional(readOnly = true)
    public List<FracaoDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FracaoDTO obter(Long id) {
        return toDTO(buscar(id));
    }

    public FracaoDTO criar(FracaoDTO dto) {
        Condominio c = condominioRepository.findById(dto.getCondominioId())
                .orElseThrow(() -> new ResourceNotFoundException("Condominio nao encontrado: " + dto.getCondominioId()));

        validarLimitePlano(c);
        validarPermilagem(dto.getCondominioId(), dto.getPermilagem(), null);

        Fracao f = new Fracao();
        aplicar(dto, f, c);
        return toDTO(repository.save(f));
    }

    public FracaoDTO atualizar(Long id, FracaoDTO dto) {
        Fracao f = buscar(id);
        Condominio c = condominioRepository.findById(dto.getCondominioId())
                .orElseThrow(() -> new ResourceNotFoundException("Condominio nao encontrado: " + dto.getCondominioId()));

        validarPermilagem(dto.getCondominioId(), dto.getPermilagem(), id);
        aplicar(dto, f, c);
        return toDTO(repository.save(f));
    }

    public void apagar(Long id) {
        repository.delete(buscar(id));
    }

    /** Regra SaaS: o numero de fracoes da empresa nao pode exceder o limite do plano. */
    private void validarLimitePlano(Condominio condominio) {
        if (condominio.getEmpresa() == null) {
            return;
        }
        Plano plano = condominio.getEmpresa().getPlano();
        long atuais = repository.countByCondominioEmpresaId(condominio.getEmpresa().getId());
        if (atuais >= plano.getLimiteFracoes()) {
            throw new IllegalArgumentException(
                    "Limite do plano " + plano + " atingido (" + plano.getLimiteFracoes()
                            + " fracoes). Faca upgrade para adicionar mais.");
        }
    }

    /** Regra de negocio: a soma das permilagens de um condominio nao pode exceder 1000. */
    private void validarPermilagem(Long condominioId, BigDecimal nova, Long fracaoIdExcluir) {
        if (nova == null) {
            return;
        }
        BigDecimal soma = repository.findByCondominioId(condominioId).stream()
                .filter(f -> fracaoIdExcluir == null || !f.getId().equals(fracaoIdExcluir))
                .map(Fracao::getPermilagem)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(nova);
        if (soma.compareTo(LIMITE_PERMILAGEM) > 0) {
            throw new IllegalArgumentException(
                    "A soma das permilagens (" + soma + ") excede o maximo de 1000 para o condominio.");
        }
    }

    private void aplicar(FracaoDTO dto, Fracao f, Condominio c) {
        f.setNumero(dto.getNumero());
        f.setPiso(dto.getPiso());
        f.setPermilagem(dto.getPermilagem());
        f.setTipologia(dto.getTipologia());
        f.setCondominio(c);
        if (dto.getEdificioId() != null) {
            Edificio e = edificioRepository.findById(dto.getEdificioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Edificio nao encontrado: " + dto.getEdificioId()));
            f.setEdificio(e);
        } else {
            f.setEdificio(null);
        }
    }

    private Fracao buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fracao nao encontrada: " + id));
    }

    private FracaoDTO toDTO(Fracao f) {
        FracaoDTO dto = new FracaoDTO();
        dto.setId(f.getId());
        dto.setNumero(f.getNumero());
        dto.setPiso(f.getPiso());
        dto.setPermilagem(f.getPermilagem());
        dto.setTipologia(f.getTipologia());
        dto.setEdificioId(f.getEdificio() != null ? f.getEdificio().getId() : null);
        dto.setCondominioId(f.getCondominio() != null ? f.getCondominio().getId() : null);
        return dto;
    }
}
