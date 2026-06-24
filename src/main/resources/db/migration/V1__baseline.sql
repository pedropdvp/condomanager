-- =====================================================================
-- V1 — Baseline do esquema CondoManager (MySQL 8)
-- Convenções (docs/DATABASE_SCHEMA.md):
--   - Tabelas em snake_case
--   - PK BIGINT AUTO_INCREMENT com prefixo id_
--   - created_at / updated_at em todas as tabelas
-- Esta migração cria apenas a raiz do tenant (empresa_gestao).
-- As restantes tabelas serão acrescentadas nas fases seguintes.
-- =====================================================================

CREATE TABLE empresa_gestao (
    id_empresa  BIGINT       NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(150) NOT NULL,
    nif         VARCHAR(20)  NOT NULL,
    email       VARCHAR(150) NOT NULL,
    telefone    VARCHAR(30),
    morada      VARCHAR(255),
    -- ENUM de domínio representado como VARCHAR (portável e compatível com JPA;
    -- os valores válidos são garantidos pelo enum EstadoEmpresa).
    estado      VARCHAR(20)  NOT NULL DEFAULT 'ATIVO',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_empresa_gestao PRIMARY KEY (id_empresa),
    CONSTRAINT uk_empresa_gestao_nif UNIQUE (nif),
    CONSTRAINT uk_empresa_gestao_email UNIQUE (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
