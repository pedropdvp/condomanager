package com.condomanager.service;

import com.condomanager.dto.ResultadoVotacaoDTO;
import com.condomanager.dto.VotacaoDTO;
import com.condomanager.dto.VotoDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.*;
import com.condomanager.model.enums.EstadoVotacao;
import com.condomanager.model.enums.RespostaVoto;
import com.condomanager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VotacaoService {

    private final VotacaoRepository votacaoRepository;
    private final VotoRepository votoRepository;
    private final CondominioRepository condominioRepository;
    private final ReuniaoRepository reuniaoRepository;
    private final CondominoRepository condominoRepository;

    public VotacaoService(VotacaoRepository votacaoRepository,
                          VotoRepository votoRepository,
                          CondominioRepository condominioRepository,
                          ReuniaoRepository reuniaoRepository,
                          CondominoRepository condominoRepository) {
        this.votacaoRepository = votacaoRepository;
        this.votoRepository = votoRepository;
        this.condominioRepository = condominioRepository;
        this.reuniaoRepository = reuniaoRepository;
        this.condominoRepository = condominoRepository;
    }

    @Transactional(readOnly = true)
    public List<VotacaoDTO> listar() {
        return votacaoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VotacaoDTO obter(Long id) {
        return toDTO(buscar(id));
    }

    public VotacaoDTO criar(VotacaoDTO dto) {
        Condominio c = condominioRepository.findById(dto.getCondominioId())
                .orElseThrow(() -> new ResourceNotFoundException("Condominio nao encontrado: " + dto.getCondominioId()));
        Votacao v = new Votacao();
        v.setTema(dto.getTema());
        v.setDataInicio(dto.getDataInicio() != null ? dto.getDataInicio() : LocalDateTime.now());
        v.setDataFim(dto.getDataFim());
        v.setEstado(EstadoVotacao.ABERTA);
        v.setCondominio(c);
        if (dto.getReuniaoId() != null) {
            Reuniao r = reuniaoRepository.findById(dto.getReuniaoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reuniao nao encontrada: " + dto.getReuniaoId()));
            v.setReuniao(r);
        }
        return toDTO(votacaoRepository.save(v));
    }

    /** Encerra a votacao (RF12). */
    public VotacaoDTO encerrar(Long id) {
        Votacao v = buscar(id);
        v.setEstado(EstadoVotacao.ENCERRADA);
        v.setDataFim(LocalDateTime.now());
        return toDTO(votacaoRepository.save(v));
    }

    /** Regista um voto. Um condomino so pode votar uma vez e apenas em votacao ABERTA. */
    public ResultadoVotacaoDTO votar(VotoDTO dto) {
        Votacao v = buscar(dto.getVotacaoId());
        if (v.getEstado() != EstadoVotacao.ABERTA) {
            throw new IllegalArgumentException("A votacao esta encerrada.");
        }
        if (votoRepository.existsByVotacaoIdAndCondominoId(v.getId(), dto.getCondominoId())) {
            throw new IllegalArgumentException("Este condomino ja votou nesta votacao.");
        }
        Condomino condomino = condominoRepository.findById(dto.getCondominoId())
                .orElseThrow(() -> new ResourceNotFoundException("Condomino nao encontrado: " + dto.getCondominoId()));

        Voto voto = new Voto();
        voto.setResposta(dto.getResposta());
        voto.setVotacao(v);
        voto.setCondomino(condomino);
        votoRepository.save(voto);

        return contar(v.getId());
    }

    @Transactional(readOnly = true)
    public ResultadoVotacaoDTO contar(Long votacaoId) {
        Votacao v = buscar(votacaoId);
        long sim = votoRepository.countByVotacaoIdAndResposta(votacaoId, RespostaVoto.SIM);
        long nao = votoRepository.countByVotacaoIdAndResposta(votacaoId, RespostaVoto.NAO);
        long abst = votoRepository.countByVotacaoIdAndResposta(votacaoId, RespostaVoto.ABSTENCAO);
        return new ResultadoVotacaoDTO(votacaoId, v.getTema(), sim, nao, abst);
    }

    private Votacao buscar(Long id) {
        return votacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Votacao nao encontrada: " + id));
    }

    private VotacaoDTO toDTO(Votacao v) {
        VotacaoDTO dto = new VotacaoDTO();
        dto.setId(v.getId());
        dto.setTema(v.getTema());
        dto.setDataInicio(v.getDataInicio());
        dto.setDataFim(v.getDataFim());
        dto.setEstado(v.getEstado());
        dto.setReuniaoId(v.getReuniao() != null ? v.getReuniao().getId() : null);
        dto.setCondominioId(v.getCondominio() != null ? v.getCondominio().getId() : null);
        return dto;
    }
}
