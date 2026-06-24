package com.condomanager.service;

import com.condomanager.dto.EdificioCreateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.EdificioMapper;
import com.condomanager.model.Condominio;
import com.condomanager.model.Edificio;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.EdificioRepository;
import com.condomanager.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdificioServiceTest {

    @Mock
    private EdificioRepository repository;
    @Mock
    private CondominioRepository condominioRepository;

    private EdificioService service;
    private final EdificioMapper mapper = new EdificioMapper();

    @BeforeEach
    void setUp() {
        service = new EdificioService(repository, condominioRepository, mapper);
    }

    @AfterEach
    void limpar() {
        TenantContext.clear();
    }

    @Test
    void criarComCondominioDeOutroTenantDeve404() {
        TenantContext.setTenantId(5L);
        when(condominioRepository.findByIdAndIdEmpresa(99L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(new EdificioCreateDTO(99L, "Bloco A", "A", 4)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void criarValidoDevePersistir() {
        TenantContext.setTenantId(5L);
        Condominio condominio = new Condominio();
        condominio.setId(7L);
        condominio.setIdEmpresa(5L);
        when(condominioRepository.findByIdAndIdEmpresa(7L, 5L)).thenReturn(Optional.of(condominio));
        when(repository.save(any(Edificio.class))).thenAnswer(inv -> {
            Edificio e = inv.getArgument(0);
            e.setId(1L);
            e.setIdEmpresa(5L);
            return e;
        });

        var response = service.criar(new EdificioCreateDTO(7L, "Bloco A", "A", 4));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.condominioId()).isEqualTo(7L);
    }
}
