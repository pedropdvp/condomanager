-- =====================================================================
-- V13 — Votações e Votos (multi-tenant)
-- O peso de cada voto é a permilagem da fração do condómino; a contagem
-- aplica as maiorias do regime de propriedade horizontal (docs/LEGAL_RULES.md §6).
-- =====================================================================

CREATE TABLE votacao (
    id_votacao   BIGINT       NOT NULL AUTO_INCREMENT,
    id_empresa   BIGINT       NOT NULL,
    id_reuniao   BIGINT       NOT NULL,
    tema         VARCHAR(255) NOT NULL,
    data_inicio  DATETIME,
    data_fim     DATETIME,
    tipo_maioria VARCHAR(30)  NOT NULL DEFAULT 'MAIORIA_SIMPLES',
    estado       VARCHAR(20)  NOT NULL DEFAULT 'CRIADA',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_votacao PRIMARY KEY (id_votacao),
    CONSTRAINT fk_votacao_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_votacao_reuniao FOREIGN KEY (id_reuniao) REFERENCES reuniao (id_reuniao),
    INDEX idx_votacao_empresa (id_empresa),
    INDEX idx_votacao_reuniao (id_reuniao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE voto (
    id_voto      BIGINT      NOT NULL AUTO_INCREMENT,
    id_empresa   BIGINT      NOT NULL,
    id_votacao   BIGINT      NOT NULL,
    id_condomino BIGINT      NOT NULL,
    resposta     VARCHAR(20) NOT NULL,
    created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_voto PRIMARY KEY (id_voto),
    CONSTRAINT fk_voto_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_voto_votacao FOREIGN KEY (id_votacao) REFERENCES votacao (id_votacao),
    CONSTRAINT fk_voto_condomino FOREIGN KEY (id_condomino) REFERENCES condomino (id_condomino),
    CONSTRAINT uk_voto_votacao_condomino UNIQUE (id_votacao, id_condomino),
    INDEX idx_voto_empresa (id_empresa),
    INDEX idx_voto_votacao (id_votacao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
