-- =====================================================================
--  CondoManager SaaS - Esquema da Base de Dados (MySQL 8)
-- ---------------------------------------------------------------------
--  Gerado a partir das entidades JPA (com.condomanager.model).
--  Como importar no MySQL Workbench:
--    1. Abrir o MySQL Workbench e ligar ao servidor.
--    2. Server > Data Import  (ou)  File > Open SQL Script...
--    3. Selecionar este ficheiro e clicar no raio (Execute / Run).
--    4. Para visualizar o diagrama (ER): Database > Reverse Engineer...
--       escolher o schema 'condomanager' depois de o executar.
--
--  Charset: utf8mb4 | Engine: InnoDB | Compatível com Spring/Hibernate.
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS condomanager
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE condomanager;

-- =====================================================================
--  1. EMPRESA_GESTAO  (raiz multi-tenant / SaaS)
-- =====================================================================
DROP TABLE IF EXISTS utilizador_permissao;
DROP TABLE IF EXISTS utilizador_perfil;
DROP TABLE IF EXISTS historico;
DROP TABLE IF EXISTS mensagem;
DROP TABLE IF EXISTS voto;
DROP TABLE IF EXISTS votacao;
DROP TABLE IF EXISTS reuniao;
DROP TABLE IF EXISTS ata;
DROP TABLE IF EXISTS documento;
DROP TABLE IF EXISTS ocorrencia;
DROP TABLE IF EXISTS pagamento;
DROP TABLE IF EXISTS quota;
DROP TABLE IF EXISTS despesa;
DROP TABLE IF EXISTS condomino;
DROP TABLE IF EXISTS fracao;
DROP TABLE IF EXISTS edificio;
DROP TABLE IF EXISTS condominio;
DROP TABLE IF EXISTS utilizador;
DROP TABLE IF EXISTS perfil;
DROP TABLE IF EXISTS empresa_gestao;

CREATE TABLE empresa_gestao (
    id_empresa  BIGINT       NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(150) NOT NULL,
    nif         VARCHAR(20)  NOT NULL,
    email       VARCHAR(150) NOT NULL,
    telefone    VARCHAR(20)  NULL,
    morada      VARCHAR(255) NULL,
    estado      VARCHAR(20)  NULL DEFAULT 'ATIVA',
    plano       ENUM('STARTER','BUSINESS','ENTERPRISE') NOT NULL DEFAULT 'STARTER',
    PRIMARY KEY (id_empresa),
    UNIQUE KEY uk_empresa_nif (nif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
--  2. PERFIL  (roles do sistema)
-- =====================================================================
CREATE TABLE perfil (
    id_perfil  BIGINT NOT NULL AUTO_INCREMENT,
    nome       ENUM('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO') NOT NULL,
    descricao  VARCHAR(150) NULL,
    PRIMARY KEY (id_perfil),
    UNIQUE KEY uk_perfil_nome (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
--  3. UTILIZADOR
-- =====================================================================
CREATE TABLE utilizador (
    id_utilizador BIGINT       NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(150) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    password      VARCHAR(60)  NOT NULL,           -- hash BCrypt
    ativo         TINYINT(1)   NOT NULL DEFAULT 1,
    id_empresa    BIGINT       NULL,
    PRIMARY KEY (id_utilizador),
    UNIQUE KEY uk_utilizador_email (email),
    KEY idx_utilizador_empresa (id_empresa),
    CONSTRAINT fk_utilizador_empresa FOREIGN KEY (id_empresa)
        REFERENCES empresa_gestao (id_empresa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Associacao N:N utilizador <-> perfil
CREATE TABLE utilizador_perfil (
    id_utilizador BIGINT NOT NULL,
    id_perfil     BIGINT NOT NULL,
    PRIMARY KEY (id_utilizador, id_perfil),
    KEY idx_up_perfil (id_perfil),
    CONSTRAINT fk_up_utilizador FOREIGN KEY (id_utilizador)
        REFERENCES utilizador (id_utilizador) ON DELETE CASCADE,
    CONSTRAINT fk_up_perfil FOREIGN KEY (id_perfil)
        REFERENCES perfil (id_perfil) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Permissoes granulares (Funcionalidade x Acao) por utilizador
CREATE TABLE utilizador_permissao (
    id_utilizador  BIGINT NOT NULL,
    funcionalidade ENUM('EMPRESAS','CONDOMINIOS','CONDOMINOS','UTILIZADORES','ATAS',
                        'PAGAMENTOS','REUNIOES','VOTACOES','DOCUMENTOS','MENSAGENS') NOT NULL,
    acao           ENUM('CRIAR','EDITAR','APAGAR','CONSULTAR') NOT NULL,
    PRIMARY KEY (id_utilizador, funcionalidade, acao),
    CONSTRAINT fk_perm_utilizador FOREIGN KEY (id_utilizador)
        REFERENCES utilizador (id_utilizador) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
--  4. CONDOMINIO
-- =====================================================================
CREATE TABLE condominio (
    id_condominio   BIGINT       NOT NULL AUTO_INCREMENT,
    nome            VARCHAR(150) NOT NULL,
    morada          VARCHAR(255) NULL,
    orcamento_anual DECIMAL(12,2) NULL DEFAULT 0.00,
    id_empresa      BIGINT       NOT NULL,
    PRIMARY KEY (id_condominio),
    KEY idx_condominio_empresa (id_empresa),
    CONSTRAINT fk_condominio_empresa FOREIGN KEY (id_empresa)
        REFERENCES empresa_gestao (id_empresa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
--  5. EDIFICIO
-- =====================================================================
CREATE TABLE edificio (
    id_edificio   BIGINT       NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(150) NOT NULL,
    morada        VARCHAR(255) NULL,
    num_pisos     INT          NULL,
    id_condominio BIGINT       NOT NULL,
    PRIMARY KEY (id_edificio),
    KEY idx_edificio_condominio (id_condominio),
    CONSTRAINT fk_edificio_condominio FOREIGN KEY (id_condominio)
        REFERENCES condominio (id_condominio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
--  6. FRACAO
-- =====================================================================
CREATE TABLE fracao (
    id_fracao     BIGINT      NOT NULL AUTO_INCREMENT,
    numero        VARCHAR(20) NOT NULL,
    piso          INT         NULL,
    permilagem    DECIMAL(7,3) NULL,                -- soma por condominio = 1000
    tipologia     VARCHAR(30) NULL,
    id_edificio   BIGINT      NULL,
    id_condominio BIGINT      NOT NULL,
    PRIMARY KEY (id_fracao),
    KEY idx_fracao_edificio (id_edificio),
    KEY idx_fracao_condominio (id_condominio),
    CONSTRAINT fk_fracao_edificio FOREIGN KEY (id_edificio)
        REFERENCES edificio (id_edificio),
    CONSTRAINT fk_fracao_condominio FOREIGN KEY (id_condominio)
        REFERENCES condominio (id_condominio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
--  7. CONDOMINO
-- =====================================================================
CREATE TABLE condomino (
    id_condomino BIGINT       NOT NULL AUTO_INCREMENT,
    nome         VARCHAR(150) NOT NULL,
    nif          VARCHAR(20)  NULL,
    email        VARCHAR(150) NULL,
    telefone     VARCHAR(20)  NULL,
    tipo         ENUM('PROPRIETARIO','INQUILINO') NULL DEFAULT 'PROPRIETARIO',
    id_fracao    BIGINT       NULL,
    PRIMARY KEY (id_condomino),
    KEY idx_condomino_fracao (id_fracao),
    CONSTRAINT fk_condomino_fracao FOREIGN KEY (id_fracao)
        REFERENCES fracao (id_fracao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
--  8. DESPESA
-- =====================================================================
CREATE TABLE despesa (
    id_despesa    BIGINT       NOT NULL AUTO_INCREMENT,
    descricao     VARCHAR(255) NOT NULL,
    valor         DECIMAL(10,2) NOT NULL,
    data          DATE         NOT NULL,
    categoria     VARCHAR(50)  NULL,
    estado        ENUM('PENDENTE','APROVADA','PAGA') NULL DEFAULT 'PENDENTE',
    id_condominio BIGINT       NOT NULL,
    PRIMARY KEY (id_despesa),
    KEY idx_despesa_condominio (id_condominio),
    CONSTRAINT fk_despesa_condominio FOREIGN KEY (id_condominio)
        REFERENCES condominio (id_condominio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
--  9. QUOTA
-- =====================================================================
CREATE TABLE quota (
    id_quota   BIGINT        NOT NULL AUTO_INCREMENT,
    mes        INT           NOT NULL,
    ano        INT           NOT NULL,
    valor      DECIMAL(10,2) NOT NULL,
    estado     ENUM('PENDENTE','PAGA','ATRASO') NULL DEFAULT 'PENDENTE',
    id_fracao  BIGINT        NOT NULL,
    PRIMARY KEY (id_quota),
    KEY idx_quota_fracao (id_fracao),
    CONSTRAINT fk_quota_fracao FOREIGN KEY (id_fracao)
        REFERENCES fracao (id_fracao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- 10. PAGAMENTO
-- =====================================================================
CREATE TABLE pagamento (
    id_pagamento BIGINT        NOT NULL AUTO_INCREMENT,
    valor        DECIMAL(10,2) NOT NULL,
    data         DATE          NOT NULL,
    metodo       ENUM('MBWAY','TRANSFERENCIA','PAYPAL') NULL,
    estado       VARCHAR(20)   NULL DEFAULT 'CONFIRMADO',
    id_quota     BIGINT        NOT NULL,
    PRIMARY KEY (id_pagamento),
    KEY idx_pagamento_quota (id_quota),
    CONSTRAINT fk_pagamento_quota FOREIGN KEY (id_quota)
        REFERENCES quota (id_quota)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- 11. OCORRENCIA
-- =====================================================================
CREATE TABLE ocorrencia (
    id_ocorrencia BIGINT       NOT NULL AUTO_INCREMENT,
    titulo        VARCHAR(150) NOT NULL,
    descricao     TEXT         NULL,
    estado        ENUM('ABERTA','EM_ANALISE','RESOLVIDA','FECHADA') NULL DEFAULT 'ABERTA',
    prioridade    ENUM('BAIXA','MEDIA','ALTA') NULL DEFAULT 'MEDIA',
    data_registo  DATETIME     NULL,
    id_condomino  BIGINT       NULL,
    PRIMARY KEY (id_ocorrencia),
    KEY idx_ocorrencia_condomino (id_condomino),
    CONSTRAINT fk_ocorrencia_condomino FOREIGN KEY (id_condomino)
        REFERENCES condomino (id_condomino)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- 12. DOCUMENTO
-- =====================================================================
CREATE TABLE documento (
    id_documento  BIGINT       NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(150) NOT NULL,
    tipo          VARCHAR(30)  NULL,                -- REGULAMENTO, CONTRATO, ORCAMENTO, ATA, FATURA, APOLICE
    ficheiro      VARCHAR(255) NULL,
    data_upload   DATETIME     NULL,
    id_condominio BIGINT       NOT NULL,
    PRIMARY KEY (id_documento),
    KEY idx_documento_condominio (id_condominio),
    CONSTRAINT fk_documento_condominio FOREIGN KEY (id_condominio)
        REFERENCES condominio (id_condominio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- 13. ATA
-- =====================================================================
CREATE TABLE ata (
    id_ata        BIGINT       NOT NULL AUTO_INCREMENT,
    titulo        VARCHAR(150) NOT NULL,
    descricao     TEXT         NULL,
    data_reuniao  DATE         NULL,
    ficheiro      VARCHAR(255) NULL,
    arquivada     TINYINT(1)   NOT NULL DEFAULT 0,
    id_condominio BIGINT       NOT NULL,
    PRIMARY KEY (id_ata),
    KEY idx_ata_condominio (id_condominio),
    CONSTRAINT fk_ata_condominio FOREIGN KEY (id_condominio)
        REFERENCES condominio (id_condominio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- 14. REUNIAO
-- =====================================================================
CREATE TABLE reuniao (
    id_reuniao    BIGINT       NOT NULL AUTO_INCREMENT,
    assunto       VARCHAR(150) NULL,
    data          DATE         NOT NULL,
    hora          TIME         NULL,
    local         VARCHAR(150) NULL,
    estado        ENUM('AGENDADA','REALIZADA','CANCELADA') NULL DEFAULT 'AGENDADA',
    id_condominio BIGINT       NOT NULL,
    PRIMARY KEY (id_reuniao),
    KEY idx_reuniao_condominio (id_condominio),
    CONSTRAINT fk_reuniao_condominio FOREIGN KEY (id_condominio)
        REFERENCES condominio (id_condominio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- 15. VOTACAO
-- =====================================================================
CREATE TABLE votacao (
    id_votacao    BIGINT       NOT NULL AUTO_INCREMENT,
    tema          VARCHAR(255) NOT NULL,
    data_inicio   DATETIME     NULL,
    data_fim      DATETIME     NULL,
    estado        ENUM('ABERTA','ENCERRADA') NULL DEFAULT 'ABERTA',
    id_reuniao    BIGINT       NULL,
    id_condominio BIGINT       NOT NULL,
    PRIMARY KEY (id_votacao),
    KEY idx_votacao_reuniao (id_reuniao),
    KEY idx_votacao_condominio (id_condominio),
    CONSTRAINT fk_votacao_reuniao FOREIGN KEY (id_reuniao)
        REFERENCES reuniao (id_reuniao),
    CONSTRAINT fk_votacao_condominio FOREIGN KEY (id_condominio)
        REFERENCES condominio (id_condominio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- 16. VOTO  (um voto por condomino e votacao)
-- =====================================================================
CREATE TABLE voto (
    id_voto      BIGINT NOT NULL AUTO_INCREMENT,
    resposta     ENUM('SIM','NAO','ABSTENCAO') NOT NULL,
    id_votacao   BIGINT NOT NULL,
    id_condomino BIGINT NOT NULL,
    PRIMARY KEY (id_voto),
    UNIQUE KEY uk_voto_votacao_condomino (id_votacao, id_condomino),
    KEY idx_voto_condomino (id_condomino),
    CONSTRAINT fk_voto_votacao FOREIGN KEY (id_votacao)
        REFERENCES votacao (id_votacao),
    CONSTRAINT fk_voto_condomino FOREIGN KEY (id_condomino)
        REFERENCES condomino (id_condomino)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- 17. MENSAGEM
-- =====================================================================
CREATE TABLE mensagem (
    id_mensagem   BIGINT       NOT NULL AUTO_INCREMENT,
    assunto       VARCHAR(150) NOT NULL,
    conteudo      TEXT         NULL,
    data_envio    DATETIME     NULL,
    destino       VARCHAR(20)  NULL DEFAULT 'TODOS',  -- TODOS, GRUPO ou condomino especifico
    id_condomino  BIGINT       NULL,
    id_condominio BIGINT       NOT NULL,
    PRIMARY KEY (id_mensagem),
    KEY idx_mensagem_condomino (id_condomino),
    KEY idx_mensagem_condominio (id_condominio),
    CONSTRAINT fk_mensagem_condomino FOREIGN KEY (id_condomino)
        REFERENCES condomino (id_condomino),
    CONSTRAINT fk_mensagem_condominio FOREIGN KEY (id_condominio)
        REFERENCES condominio (id_condominio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- 18. HISTORICO  (auditoria - RF17 / RNF09)
-- =====================================================================
CREATE TABLE historico (
    id_historico BIGINT       NOT NULL AUTO_INCREMENT,
    utilizador   VARCHAR(150) NULL,
    operacao     VARCHAR(255) NULL,
    data_hora    DATETIME     NULL,
    PRIMARY KEY (id_historico)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
--  FIM DO ESQUEMA
-- =====================================================================
