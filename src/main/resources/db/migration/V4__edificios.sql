-- =====================================================================
-- V4 — Edifícios (filho do condomínio; multi-tenant)
-- =====================================================================

CREATE TABLE edificio (
    id_edificio   BIGINT       NOT NULL AUTO_INCREMENT,
    id_empresa    BIGINT       NOT NULL,
    id_condominio BIGINT       NOT NULL,
    nome          VARCHAR(100) NOT NULL,
    bloco         VARCHAR(50),
    numero_pisos  INT,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_edificio PRIMARY KEY (id_edificio),
    CONSTRAINT fk_edificio_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_edificio_condominio FOREIGN KEY (id_condominio) REFERENCES condominio (id_condominio),
    INDEX idx_edificio_empresa (id_empresa),
    INDEX idx_edificio_condominio (id_condominio)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
