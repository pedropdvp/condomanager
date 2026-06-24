-- =====================================================================
-- V2 — Autenticação: utilizadores, perfis e associação (RBAC)
-- Suporta a Fase 1 (docs/MODULES_ROADMAP.md).
-- =====================================================================

CREATE TABLE perfil (
    id_perfil  BIGINT      NOT NULL AUTO_INCREMENT,
    nome       VARCHAR(50) NOT NULL,
    descricao  VARCHAR(255),
    CONSTRAINT pk_perfil PRIMARY KEY (id_perfil),
    CONSTRAINT uk_perfil_nome UNIQUE (nome)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE utilizador (
    id_utilizador BIGINT       NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(150) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    password      VARCHAR(255) NOT NULL,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    -- id_empresa é NULO para o ADMIN_SISTEMA (não pertence a nenhum tenant).
    id_empresa    BIGINT,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_utilizador PRIMARY KEY (id_utilizador),
    CONSTRAINT uk_utilizador_email UNIQUE (email),
    CONSTRAINT fk_utilizador_empresa FOREIGN KEY (id_empresa)
        REFERENCES empresa_gestao (id_empresa)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE utilizador_perfil (
    id_utilizador BIGINT NOT NULL,
    id_perfil     BIGINT NOT NULL,
    CONSTRAINT pk_utilizador_perfil PRIMARY KEY (id_utilizador, id_perfil),
    CONSTRAINT fk_up_utilizador FOREIGN KEY (id_utilizador)
        REFERENCES utilizador (id_utilizador) ON DELETE CASCADE,
    CONSTRAINT fk_up_perfil FOREIGN KEY (id_perfil)
        REFERENCES perfil (id_perfil)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Perfis base (docs/DATABASE_SCHEMA.md / DOMAIN_MODEL.md)
INSERT INTO perfil (nome, descricao) VALUES
    ('ADMIN_SISTEMA',     'Administrador da plataforma SaaS'),
    ('GESTOR_EMPRESA',    'Gestor de uma empresa de gestão de condomínios'),
    ('FUNCIONARIO',       'Funcionário de uma empresa de gestão'),
    ('ADMIN_CONDOMINIO',  'Administrador de um condomínio'),
    ('CONDOMINO',         'Condómino (proprietário ou inquilino)');
