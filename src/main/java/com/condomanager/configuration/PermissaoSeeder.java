package com.condomanager.configuration;

import com.condomanager.model.Acao;
import com.condomanager.model.Funcionalidade;
import com.condomanager.model.Perfil;
import com.condomanager.model.Permissao;
import com.condomanager.repository.PerfilRepository;
import com.condomanager.repository.PermissaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Semeia as permissões granulares por defeito (matriz funcionalidade × ação) na
 * primeira vez que a tabela {@code permissao} está vazia. Corre em qualquer perfil
 * (também em produção). Idempotente: não re-semeia se já existirem permissões
 * (permite personalização persistente pelo administrador).
 *
 * <p>O seed é feito por JPA (não em SQL na migração) para evitar incompatibilidades
 * entre MySQL e motores MySQL-compatíveis (ex.: TiDB).</p>
 */
@Component
@Order(20)
public class PermissaoSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PermissaoSeeder.class);

    /** Letras: C=CRIAR, E=EDITAR, A=APAGAR, X=CONSULTAR. */
    private static final String[][] DEFAULTS = {
            {"GESTOR_EMPRESA", "EMPRESAS:CEAX,CONDOMINIOS:CEAX,CONDOMINOS:X,UTILIZADORES:CEAX,ATAS:CEAX,"
                    + "PAGAMENTOS:CX,REUNIOES:CEX,VOTACOES:CEX,DOCUMENTOS:CX,MENSAGENS:CX"},
            {"FUNCIONARIO", "EMPRESAS:X,CONDOMINIOS:CEX,CONDOMINOS:CEX,ATAS:CEX,PAGAMENTOS:CX,"
                    + "REUNIOES:X,VOTACOES:X,DOCUMENTOS:X,MENSAGENS:CX"},
            {"ADMIN_CONDOMINIO", "CONDOMINIOS:X,CONDOMINOS:CEAX,ATAS:CEAX,PAGAMENTOS:X,REUNIOES:CX,"
                    + "VOTACOES:X,DOCUMENTOS:X,MENSAGENS:CX"},
            {"CONDOMINO", "ATAS:X,PAGAMENTOS:CX,REUNIOES:X,VOTACOES:CX,DOCUMENTOS:X,MENSAGENS:C"}
    };

    private final PermissaoRepository permissaoRepository;
    private final PerfilRepository perfilRepository;

    public PermissaoSeeder(PermissaoRepository permissaoRepository, PerfilRepository perfilRepository) {
        this.permissaoRepository = permissaoRepository;
        this.perfilRepository = perfilRepository;
    }

    @Override
    public void run(String... args) {
        if (permissaoRepository.count() > 0) {
            return;
        }
        List<Permissao> todas = new ArrayList<>();
        for (String[] entrada : DEFAULTS) {
            perfilRepository.findByNome(entrada[0])
                    .ifPresent(perfil -> todas.addAll(permissoesDe(perfil, entrada[1])));
        }
        permissaoRepository.saveAll(todas);
        logger.info("Permissões por defeito semeadas: {} entradas.", todas.size());
    }

    private List<Permissao> permissoesDe(Perfil perfil, String spec) {
        List<Permissao> lista = new ArrayList<>();
        for (String parte : spec.split(",")) {
            String[] kv = parte.split(":");
            Funcionalidade funcionalidade = Funcionalidade.valueOf(kv[0]);
            for (char c : kv[1].toCharArray()) {
                Acao acao = switch (c) {
                    case 'C' -> Acao.CRIAR;
                    case 'E' -> Acao.EDITAR;
                    case 'A' -> Acao.APAGAR;
                    case 'X' -> Acao.CONSULTAR;
                    default -> null;
                };
                if (acao != null) {
                    Permissao p = new Permissao();
                    p.setPerfil(perfil);
                    p.setFuncionalidade(funcionalidade);
                    p.setAcao(acao);
                    lista.add(p);
                }
            }
        }
        return lista;
    }
}
