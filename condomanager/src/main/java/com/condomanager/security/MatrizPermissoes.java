package com.condomanager.security;

import com.condomanager.model.enums.Funcionalidade;
import com.condomanager.model.enums.NivelAcesso;
import com.condomanager.model.enums.NomePerfil;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

import static com.condomanager.model.enums.NivelAcesso.*;
import static com.condomanager.model.enums.NomePerfil.*;

/**
 * Matriz de permissoes Funcionalidade x Perfil (conforme tabela do planeamento).
 *
 * E a fonte unica de verdade apresentada ao Administrador do Sistema ao criar um acesso.
 * A APLICACAO efetiva das permissoes e feita por @PreAuthorize em cada controlador,
 * mantida em coerencia com esta matriz:
 *   - SIM      -> CRUD completo
 *   - CONSULTA -> apenas leitura (GET)
 *   - PARTICIPA-> leitura + votar (Votacoes)
 *   - RECEBE   -> apenas leitura/rececao (Mensagens, sem enviar)
 *   - NAO      -> sem acesso
 */
@Component
public class MatrizPermissoes {

    private final Map<Funcionalidade, Map<NomePerfil, NivelAcesso>> matriz =
            new EnumMap<>(Funcionalidade.class);

    public MatrizPermissoes() {
        //                                ADMIN_SIST  GESTOR  FUNCIONARIO  ADMIN_COND  CONDOMINO
        def(Funcionalidade.EMPRESAS,      SIM,        SIM,    NAO,         NAO,        NAO);
        def(Funcionalidade.CONDOMINIOS,   SIM,        SIM,    SIM,         NAO,        NAO);
        def(Funcionalidade.CONDOMINOS,    SIM,        SIM,    SIM,         SIM,        NAO);
        def(Funcionalidade.UTILIZADORES,  SIM,        SIM,    NAO,         NAO,        NAO);
        def(Funcionalidade.ATAS,          SIM,        SIM,    SIM,         SIM,        CONSULTA);
        def(Funcionalidade.PAGAMENTOS,    SIM,        SIM,    CONSULTA,    CONSULTA,   CONSULTA);
        def(Funcionalidade.REUNIOES,      SIM,        SIM,    SIM,         SIM,        CONSULTA);
        def(Funcionalidade.VOTACOES,      SIM,        SIM,    SIM,         SIM,        PARTICIPA);
        def(Funcionalidade.DOCUMENTOS,    SIM,        SIM,    SIM,         SIM,        CONSULTA);
        def(Funcionalidade.MENSAGENS,     SIM,        SIM,    SIM,         SIM,        RECEBE);
    }

    private void def(Funcionalidade f, NivelAcesso admin, NivelAcesso gestor,
                     NivelAcesso funcionario, NivelAcesso adminCondominio, NivelAcesso condomino) {
        Map<NomePerfil, NivelAcesso> linha = new EnumMap<>(NomePerfil.class);
        linha.put(ADMIN_SISTEMA, admin);
        linha.put(GESTOR, gestor);
        linha.put(FUNCIONARIO, funcionario);
        linha.put(ADMIN_CONDOMINIO, adminCondominio);
        linha.put(CONDOMINO, condomino);
        matriz.put(f, linha);
    }

    public Map<Funcionalidade, Map<NomePerfil, NivelAcesso>> getMatriz() {
        return matriz;
    }

    public NivelAcesso nivel(Funcionalidade funcionalidade, NomePerfil perfil) {
        return matriz.get(funcionalidade).get(perfil);
    }
}
