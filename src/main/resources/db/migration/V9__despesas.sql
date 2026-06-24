-- =====================================================================
-- V9 — Despesas (despesa do condomínio; multi-tenant)
-- =====================================================================

CREATE TABLE despesa (
    id_despesa    BIGINT        NOT NULL AUTO_INCREMENT,
    id_empresa    BIGINT        NOT NULL,
    id_condominio BIGINT        NOT NULL,
    descricao     VARCHAR(255)  NOT NULL,
    categoria     VARCHAR(100)  NOT NULL,
    valor         DECIMAL(10,2) NOT NULL,
    data_despesa  DATE          NOT NULL,
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_despesa PRIMARY KEY (id_despesa),
    CONSTRAINT fk_despesa_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_despesa_condominio FOREIGN KEY (id_condominio) REFERENCES condominio (id_condominio),
    INDEX idx_despesa_empresa (id_empresa),
    INDEX idx_despesa_condominio (id_condominio)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
