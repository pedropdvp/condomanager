-- =====================================================================
-- V22 — Permissões granulares por perfil (RBAC com matriz funcionalidade × ação).
-- A presença de uma linha = ação permitida. O ADMIN_SISTEMA tem acesso total
-- por código (não é semeado). Aplicado via @permissaoService.pode(...).
--
-- O seed das permissões por defeito é feito em Java (PermissaoSeeder) — evita
-- quaisquer incompatibilidades de SQL entre MySQL e MySQL-compatíveis (TiDB).
-- =====================================================================

CREATE TABLE permissao (
    id_permissao   BIGINT      NOT NULL AUTO_INCREMENT,
    id_perfil      BIGINT      NOT NULL,
    funcionalidade VARCHAR(30) NOT NULL,
    acao           VARCHAR(20) NOT NULL,
    CONSTRAINT pk_permissao PRIMARY KEY (id_permissao),
    CONSTRAINT uk_permissao UNIQUE (id_perfil, funcionalidade, acao),
    CONSTRAINT fk_permissao_perfil FOREIGN KEY (id_perfil) REFERENCES perfil (id_perfil)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
