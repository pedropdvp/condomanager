package com.condomanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Dados para criar um utilizador.
 *
 * <p>Para um GESTOR_EMPRESA, o {@code idEmpresa} é ignorado e forçado à sua própria
 * empresa. Para um ADMIN_SISTEMA, o {@code idEmpresa} indica a empresa do novo
 * utilizador (ou nulo, para outro administrador de sistema).</p>
 */
public record UtilizadorCreateDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 150)
        String email,

        @NotBlank(message = "A password é obrigatória")
        @Size(min = 6, max = 72, message = "A password deve ter entre 6 e 72 caracteres")
        String password,

        @NotEmpty(message = "Pelo menos um perfil é obrigatório")
        Set<String> perfis,

        Long idEmpresa
) {
}
