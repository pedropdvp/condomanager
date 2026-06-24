-- =====================================================================
-- V10 — Documentos (gestão documental do condomínio; multi-tenant)
-- O ficheiro é guardado no filesystem local; a coluna `ficheiro` guarda o
-- caminho relativo (ver docs/SPEC.md item E).
-- =====================================================================

CREATE TABLE documento (
    id_documento  BIGINT       NOT NULL AUTO_INCREMENT,
    id_empresa    BIGINT       NOT NULL,
    id_condominio BIGINT       NOT NULL,
    nome          VARCHAR(255) NOT NULL,
    tipo          VARCHAR(100),
    ficheiro      VARCHAR(255) NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_documento PRIMARY KEY (id_documento),
    CONSTRAINT fk_documento_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_documento_condominio FOREIGN KEY (id_condominio) REFERENCES condominio (id_condominio),
    INDEX idx_documento_empresa (id_empresa),
    INDEX idx_documento_condominio (id_condominio)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
