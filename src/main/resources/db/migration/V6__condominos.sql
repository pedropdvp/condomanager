-- =====================================================================
-- V6 — Condóminos (filho da fração; multi-tenant)
-- =====================================================================

CREATE TABLE condomino (
    id_condomino BIGINT       NOT NULL AUTO_INCREMENT,
    id_empresa   BIGINT       NOT NULL,
    id_fracao    BIGINT       NOT NULL,
    nome         VARCHAR(150) NOT NULL,
    nif          VARCHAR(20),
    email        VARCHAR(150),
    telefone     VARCHAR(30),
    -- ENUM de domínio (PROPRIETARIO, INQUILINO) representado como VARCHAR.
    tipo         VARCHAR(20)  NOT NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_condomino PRIMARY KEY (id_condomino),
    CONSTRAINT fk_condomino_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_condomino_fracao FOREIGN KEY (id_fracao) REFERENCES fracao (id_fracao),
    INDEX idx_condomino_empresa (id_empresa),
    INDEX idx_condomino_fracao (id_fracao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
