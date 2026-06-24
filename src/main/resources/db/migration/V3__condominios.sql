-- =====================================================================
-- V3 — Condomínios (primeira entidade multi-tenant: discriminador id_empresa)
-- Suporta a Fase 3 (docs/MODULES_ROADMAP.md).
-- =====================================================================

CREATE TABLE condominio (
    id_condominio   BIGINT        NOT NULL AUTO_INCREMENT,
    id_empresa      BIGINT        NOT NULL,
    nome            VARCHAR(150)  NOT NULL,
    morada          VARCHAR(255),
    orcamento_anual DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_condominio PRIMARY KEY (id_condominio),
    CONSTRAINT fk_condominio_empresa FOREIGN KEY (id_empresa)
        REFERENCES empresa_gestao (id_empresa),
    INDEX idx_condominio_empresa (id_empresa)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
