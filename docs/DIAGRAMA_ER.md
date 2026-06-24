# Diagrama Relacional — Sistema de Gestão de Condomínios

Modelo Entidade-Relação (ERD) de todas as tabelas da base de dados.
A entidade `empresa_gestao` é o **tenant** (multi-empresa): quase todas as tabelas
têm `id_empresa` para isolamento de dados por empresa.

## Diagrama completo

```mermaid
erDiagram
    empresa_gestao {
        BIGINT id_empresa PK
        VARCHAR nome
        VARCHAR nif
        VARCHAR email
        VARCHAR telefone
        VARCHAR morada
        ENUM estado
        DATETIME created_at
        DATETIME updated_at
    }

    condominio {
        BIGINT id_condominio PK
        VARCHAR nome
        VARCHAR morada
        DECIMAL orcamento_anual
        BIGINT id_empresa FK
        DATETIME created_at
        DATETIME updated_at
    }

    edificio {
        BIGINT id_edificio PK
        BIGINT id_empresa FK
        VARCHAR nome
        VARCHAR bloco
        INT numero_pisos
        BIGINT id_condominio FK
        DATETIME created_at
        DATETIME updated_at
    }

    fracao {
        BIGINT id_fracao PK
        BIGINT id_empresa FK
        VARCHAR numero
        VARCHAR tipologia
        DECIMAL permilagem
        DECIMAL area_m2
        BIGINT id_condominio FK
        BIGINT id_edificio FK
        DATETIME created_at
        DATETIME updated_at
    }

    condomino {
        BIGINT id_condomino PK
        BIGINT id_empresa FK
        VARCHAR nome
        VARCHAR nif
        VARCHAR email
        VARCHAR telefone
        ENUM tipo
        BIGINT id_fracao FK
        DATETIME created_at
        DATETIME updated_at
    }

    utilizador {
        BIGINT id_utilizador PK
        VARCHAR nome
        VARCHAR email
        VARCHAR password
        BOOLEAN ativo
        BIGINT id_empresa FK
        DATETIME created_at
        DATETIME updated_at
    }

    perfil {
        BIGINT id_perfil PK
        VARCHAR nome
        VARCHAR descricao
    }

    utilizador_perfil {
        BIGINT id_utilizador PK_FK
        BIGINT id_perfil PK_FK
    }

    quota {
        BIGINT id_quota PK
        BIGINT id_empresa FK
        INT mes
        INT ano
        DECIMAL valor
        ENUM estado
        BIGINT id_fracao FK
        DATETIME created_at
        DATETIME updated_at
    }

    pagamento {
        BIGINT id_pagamento PK
        BIGINT id_empresa FK
        DECIMAL valor
        DATETIME data_pagamento
        VARCHAR metodo
        VARCHAR estado
        BIGINT id_quota FK
    }

    despesa {
        BIGINT id_despesa PK
        BIGINT id_empresa FK
        VARCHAR descricao
        VARCHAR categoria
        DECIMAL valor
        DATE data_despesa
        BIGINT id_condominio FK
    }

    reuniao {
        BIGINT id_reuniao PK
        BIGINT id_empresa FK
        DATE data
        TIME hora
        VARCHAR local
        VARCHAR estado
        BIGINT id_condominio FK
    }

    ata {
        BIGINT id_ata PK
        BIGINT id_empresa FK
        VARCHAR titulo
        TEXT descricao
        DATE data_reuniao
        VARCHAR ficheiro
        BIGINT id_reuniao FK
    }

    votacao {
        BIGINT id_votacao PK
        BIGINT id_empresa FK
        VARCHAR tema
        DATETIME data_inicio
        DATETIME data_fim
        BIGINT id_reuniao FK
    }

    voto {
        BIGINT id_voto PK
        BIGINT id_empresa FK
        ENUM resposta
        BIGINT id_votacao FK
        BIGINT id_condomino FK
    }

    mensagem {
        BIGINT id_mensagem PK
        BIGINT id_empresa FK
        VARCHAR assunto
        TEXT conteudo
        DATETIME data_envio
        BIGINT id_utilizador_origem FK
        BIGINT id_utilizador_destino FK
    }

    documento {
        BIGINT id_documento PK
        BIGINT id_empresa FK
        VARCHAR nome
        VARCHAR tipo
        VARCHAR ficheiro
        BIGINT id_condominio FK
    }

    ocorrencia {
        BIGINT id_ocorrencia PK
        BIGINT id_empresa FK
        VARCHAR titulo
        TEXT descricao
        VARCHAR estado
        VARCHAR prioridade
        BIGINT id_condominio FK
        BIGINT id_condomino FK
    }

    historico {
        BIGINT id_historico PK
        BIGINT id_empresa FK
        VARCHAR utilizador
        VARCHAR operacao
        DATETIME data_hora
    }

    %% ---------- Relações estruturais (hierarquia do domínio) ----------
    empresa_gestao ||--o{ condominio : "gere"
    empresa_gestao ||--o{ utilizador : "emprega"

    condominio   ||--o{ edificio   : "tem"
    condominio   ||--o{ fracao     : "tem"
    condominio   ||--o{ reuniao    : "realiza"
    condominio   ||--o{ documento  : "arquiva"
    condominio   ||--o{ despesa    : "regista"
    condominio   ||--o{ ocorrencia : "regista"

    edificio     ||--o{ fracao     : "contem"

    fracao       ||--o{ quota      : "gera"
    fracao       ||--o{ condomino  : "alberga"

    quota        ||--o{ pagamento  : "liquidada por"

    reuniao      ||--o{ ata        : "produz"
    reuniao      ||--o{ votacao    : "inclui"

    votacao      ||--o{ voto       : "recebe"
    condomino    ||--o{ voto       : "emite"

    condomino    ||--o{ ocorrencia : "reporta"

    utilizador   ||--o{ mensagem   : "envia (origem)"
    utilizador   ||--o{ mensagem   : "recebe (destino)"

    %% ---------- Many-to-Many: utilizador <-> perfil ----------
    utilizador   ||--o{ utilizador_perfil : "possui"
    perfil       ||--o{ utilizador_perfil : "atribuido a"
```

## Vista de isolamento multi-empresa (tenant)

`empresa_gestao` é referenciada por `id_empresa` em **todas** as tabelas operacionais.
Para não poluir o diagrama principal, essas ligações estão resumidas aqui:

```mermaid
erDiagram
    empresa_gestao ||--o{ condominio  : id_empresa
    empresa_gestao ||--o{ edificio    : id_empresa
    empresa_gestao ||--o{ fracao      : id_empresa
    empresa_gestao ||--o{ condomino   : id_empresa
    empresa_gestao ||--o{ utilizador  : id_empresa
    empresa_gestao ||--o{ quota       : id_empresa
    empresa_gestao ||--o{ pagamento   : id_empresa
    empresa_gestao ||--o{ despesa     : id_empresa
    empresa_gestao ||--o{ reuniao     : id_empresa
    empresa_gestao ||--o{ ata         : id_empresa
    empresa_gestao ||--o{ votacao     : id_empresa
    empresa_gestao ||--o{ voto        : id_empresa
    empresa_gestao ||--o{ mensagem    : id_empresa
    empresa_gestao ||--o{ documento   : id_empresa
    empresa_gestao ||--o{ ocorrencia  : id_empresa
    empresa_gestao ||--o{ historico   : id_empresa
```

## Resumo das cardinalidades

| Lado "um" (1) | Lado "muitos" (N) | Chave estrangeira | Notas |
|---|---|---|---|
| empresa_gestao | condominio | id_empresa | Tenant |
| empresa_gestao | utilizador | id_empresa | |
| condominio | edificio | id_condominio | |
| condominio | fracao | id_condominio | |
| condominio | reuniao | id_condominio | |
| condominio | documento | id_condominio | |
| condominio | despesa | id_condominio | |
| condominio | ocorrencia | id_condominio | |
| edificio | fracao | id_edificio | |
| fracao | quota | id_fracao | |
| fracao | condomino | id_fracao | |
| quota | pagamento | id_quota | |
| reuniao | ata | id_reuniao | |
| reuniao | votacao | id_reuniao | |
| votacao | voto | id_votacao | |
| condomino | voto | id_condomino | |
| condomino | ocorrencia | id_condomino | |
| utilizador | mensagem | id_utilizador_origem | Remetente |
| utilizador | mensagem | id_utilizador_destino | Destinatário |
| utilizador ↔ perfil | utilizador_perfil | (id_utilizador, id_perfil) | M:N, PK composta |

### Notação (Crow's Foot / Mermaid)
- `||--o{` → **um-para-muitos** (1:N): o "1" é obrigatório, o "muitos" é opcional (0..N).
- `utilizador_perfil` é a **tabela de junção** que resolve o M:N entre `utilizador` e `perfil`, com chave primária composta `(id_utilizador, id_perfil)`.
- `mensagem` tem **duas** FKs para `utilizador` (origem e destino) — auto-relação dupla.
- `voto` e `ocorrencia` ligam-se a **dois pais** (votacao+condomino / condominio+condomino).
