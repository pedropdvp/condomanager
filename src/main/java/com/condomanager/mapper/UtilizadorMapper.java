package com.condomanager.mapper;

import com.condomanager.dto.UtilizadorResponse;
import com.condomanager.model.Perfil;
import com.condomanager.model.Utilizador;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Conversão de {@link Utilizador} para a sua representação pública.
 */
@Component
public class UtilizadorMapper {

    public UtilizadorResponse toResponse(Utilizador utilizador) {
        List<String> perfis = utilizador.getPerfis().stream()
                .map(Perfil::getNome)
                .sorted(Comparator.naturalOrder())
                .toList();
        return new UtilizadorResponse(
                utilizador.getId(),
                utilizador.getNome(),
                utilizador.getEmail(),
                utilizador.isAtivo(),
                utilizador.getIdEmpresa(),
                utilizador.getIdCondomino(),
                perfis
        );
    }
}
