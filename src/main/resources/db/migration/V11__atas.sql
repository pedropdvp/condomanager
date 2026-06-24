-- =====================================================================
-- V11 — Atas (documento oficial de reunião; multi-tenant)
-- id_reuniao fica nullable e SEM chave estrangeira nesta fase; a FK para
-- `reuniao` é adicionada na V12 (Reuniões), respeitando a ordem do roadmap.
-- =====================================================================

CREATE TABLE ata (
    id_ata       BIGINT       NOT NULL AUTO_INCREMENT,
    id_empresa   BIGINT       NOT NULL,
    id_reuniao   BIGINT,
    titulo       VARCHAR(200) NOT NULL,
    descricao    TEXT,
    data_reuniao DATE         NOT NULL,
    ficheiro     VARCHAR(255),
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_ata PRIMARY KEY (id_ata),
    CONSTRAINT fk_ata_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    INDEX idx_ata_empresa (id_empresa),
    INDEX idx_ata_reuniao (id_reuniao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
