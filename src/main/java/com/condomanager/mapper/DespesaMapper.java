package com.condomanager.mapper;

import com.condomanager.dto.DespesaCreateDTO;
import com.condomanager.dto.DespesaResponse;
import com.condomanager.dto.DespesaUpdateDTO;
import com.condomanager.model.Condominio;
import com.condomanager.model.Despesa;
import org.springframework.stereotype.Component;

/**
 * Conversão entre {@link Despesa} e os respetivos DTOs.
 */
@Component
public class DespesaMapper {

    public Despesa toEntity(DespesaCreateDTO dto, Condominio condominio) {
        Despesa despesa = new Despesa();
        despesa.setCondominio(condominio);
        despesa.setDescricao(dto.descricao());
        despesa.setCategoria(dto.categoria());
        despesa.setValor(dto.valor());
        despesa.setDataDespesa(dto.dataDespesa());
        return despesa;
    }

    public void aplicarAtualizacao(Despesa despesa, DespesaUpdateDTO dto) {
        despesa.setDescricao(dto.descricao());
        despesa.setCategoria(dto.categoria());
        despesa.setValor(dto.valor());
        despesa.setDataDespesa(dto.dataDespesa());
    }

    public DespesaResponse toResponse(Despesa despesa) {
        return new DespesaResponse(
                despesa.getId(),
                despesa.getCondominio().getId(),
                despesa.getDescricao(),
                despesa.getCategoria(),
                despesa.getValor(),
                despesa.getDataDespesa(),
                despesa.getEstado(),
                despesa.getIdEmpresa(),
                despesa.getCreatedAt(),
                despesa.getUpdatedAt()
        );
    }
}
