-- =====================================================================
-- V22 — Permissões granulares por perfil (RBAC com matriz funcionalidade × ação).
-- A presença de uma linha = ação permitida. O ADMIN_SISTEMA tem acesso total
-- por código (não é semeado). Aplicado via @permissaoService.pode(...).
-- =====================================================================

CREATE TABLE permissao (
    id_permissao   BIGINT      NOT NULL AUTO_INCREMENT,
    id_perfil      BIGINT      NOT NULL,
    funcionalidade VARCHAR(30) NOT NULL,
    acao           VARCHAR(20) NOT NULL,
    CONSTRAINT pk_permissao PRIMARY KEY (id_permissao),
    CONSTRAINT uk_permissao UNIQUE (id_perfil, funcionalidade, acao),
    CONSTRAINT fk_permissao_perfil FOREIGN KEY (id_perfil)
        REFERENCES perfil (id_perfil) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Seed das permissões por defeito (matriz por perfil). ADMIN_SISTEMA não é semeado.
INSERT INTO permissao (id_perfil, funcionalidade, acao)
SELECT p.id_perfil, x.funcionalidade, x.acao
FROM perfil p JOIN (
    SELECT 'GESTOR_EMPRESA' nome, 'EMPRESAS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'EMPRESAS' funcionalidade, 'EDITAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'EMPRESAS' funcionalidade, 'APAGAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'EMPRESAS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'CONDOMINIOS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'CONDOMINIOS' funcionalidade, 'EDITAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'CONDOMINIOS' funcionalidade, 'APAGAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'CONDOMINIOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'CONDOMINOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'UTILIZADORES' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'UTILIZADORES' funcionalidade, 'EDITAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'UTILIZADORES' funcionalidade, 'APAGAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'UTILIZADORES' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'ATAS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'ATAS' funcionalidade, 'EDITAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'ATAS' funcionalidade, 'APAGAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'ATAS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'PAGAMENTOS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'PAGAMENTOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'REUNIOES' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'REUNIOES' funcionalidade, 'EDITAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'REUNIOES' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'VOTACOES' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'VOTACOES' funcionalidade, 'EDITAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'VOTACOES' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'DOCUMENTOS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'DOCUMENTOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'MENSAGENS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'GESTOR_EMPRESA' nome, 'MENSAGENS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'EMPRESAS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'CONDOMINIOS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'CONDOMINIOS' funcionalidade, 'EDITAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'CONDOMINIOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'CONDOMINOS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'CONDOMINOS' funcionalidade, 'EDITAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'CONDOMINOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'ATAS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'ATAS' funcionalidade, 'EDITAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'ATAS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'PAGAMENTOS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'PAGAMENTOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'REUNIOES' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'VOTACOES' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'DOCUMENTOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'MENSAGENS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'FUNCIONARIO' nome, 'MENSAGENS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'CONDOMINIOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'CONDOMINOS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'CONDOMINOS' funcionalidade, 'EDITAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'CONDOMINOS' funcionalidade, 'APAGAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'CONDOMINOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'ATAS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'ATAS' funcionalidade, 'EDITAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'ATAS' funcionalidade, 'APAGAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'ATAS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'PAGAMENTOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'REUNIOES' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'REUNIOES' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'VOTACOES' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'DOCUMENTOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'MENSAGENS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'ADMIN_CONDOMINIO' nome, 'MENSAGENS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'CONDOMINO' nome, 'ATAS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'CONDOMINO' nome, 'PAGAMENTOS' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'CONDOMINO' nome, 'PAGAMENTOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'CONDOMINO' nome, 'REUNIOES' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'CONDOMINO' nome, 'VOTACOES' funcionalidade, 'CRIAR' acao    UNION ALL
    SELECT 'CONDOMINO' nome, 'VOTACOES' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'CONDOMINO' nome, 'DOCUMENTOS' funcionalidade, 'CONSULTAR' acao    UNION ALL
    SELECT 'CONDOMINO' nome, 'MENSAGENS' funcionalidade, 'CRIAR' acao
) x ON p.nome = x.nome;
