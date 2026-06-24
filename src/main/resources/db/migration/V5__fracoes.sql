-- =====================================================================
-- V5 — Frações (filho do edifício/condomínio; multi-tenant)
-- =====================================================================

CREATE TABLE fracao (
    id_fracao     BIGINT       NOT NULL AUTO_INCREMENT,
    id_empresa    BIGINT       NOT NULL,
    id_condominio BIGINT       NOT NULL,
    id_edificio   BIGINT       NOT NULL,
    numero        VARCHAR(20)  NOT NULL,
    tipologia     VARCHAR(20),
    -- Permilagem em milésimos (0–1000), base do rateio de quotas e do peso de voto
    -- (ver docs/LEGAL_RULES.md §2).
    permilagem    DECIMAL(8,4) NOT NULL DEFAULT 0.0000,
    area_m2       DECIMAL(8,2),
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_fracao PRIMARY KEY (id_fracao),
    CONSTRAINT fk_fracao_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_fracao_condominio FOREIGN KEY (id_condominio) REFERENCES condominio (id_condominio),
    CONSTRAINT fk_fracao_edificio FOREIGN KEY (id_edificio) REFERENCES edificio (id_edificio),
    INDEX idx_fracao_empresa (id_empresa),
    INDEX idx_fracao_condominio (id_condominio),
    INDEX idx_fracao_edificio (id_edificio)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
