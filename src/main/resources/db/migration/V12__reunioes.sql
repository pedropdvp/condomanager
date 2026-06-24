-- =====================================================================
-- V12 — Reuniões (assembleias do condomínio; multi-tenant)
-- Acrescenta também a FK ata.id_reuniao -> reuniao (adiada da V11).
-- =====================================================================

CREATE TABLE reuniao (
    id_reuniao      BIGINT       NOT NULL AUTO_INCREMENT,
    id_empresa      BIGINT       NOT NULL,
    id_condominio   BIGINT       NOT NULL,
    data            DATE         NOT NULL,
    hora            TIME,
    local           VARCHAR(255),
    ordem_trabalhos TEXT,
    estado          VARCHAR(20)  NOT NULL DEFAULT 'AGENDADA',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_reuniao PRIMARY KEY (id_reuniao),
    CONSTRAINT fk_reuniao_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_reuniao_condominio FOREIGN KEY (id_condominio) REFERENCES condominio (id_condominio),
    INDEX idx_reuniao_empresa (id_empresa),
    INDEX idx_reuniao_condominio (id_condominio)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Liga as atas às reuniões (a coluna id_reuniao foi criada na V11, sem FK).
ALTER TABLE ata
    ADD CONSTRAINT fk_ata_reuniao FOREIGN KEY (id_reuniao) REFERENCES reuniao (id_reuniao);
