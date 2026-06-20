# Base de Dados — CondoManager

Scripts SQL para criar e popular a base de dados do CondoManager no **MySQL 8** (testado com MySQL Workbench).

## Ficheiros

| Ficheiro | Descrição |
|----------|-----------|
| `condomanager_mysql.sql` | Cria a base de dados `condomanager` e as 19 tabelas (chaves primárias, estrangeiras, índices e `ENUM`s). |
| `condomanager_dados_exemplo.sql` | Insere dados de teste (empresa, condomínio, frações, condóminos, quotas, etc.) e 2 utilizadores com login. |

## Ordem de importação

> ⚠️ Ambos os scripts **apagam dados existentes** (`DROP TABLE` / `TRUNCATE`). Usar apenas em base de dados nova ou de testes.

1. **`condomanager_mysql.sql`** — cria o esquema.
2. **`condomanager_dados_exemplo.sql`** — insere os dados de exemplo *(opcional)*.

### No MySQL Workbench
1. Ligar ao servidor MySQL.
2. **File → Open SQL Script…** → escolher o ficheiro.
3. Clicar no raio (⚡ *Execute*).
4. Repetir para o segundo ficheiro.
5. Para ver o diagrama ER: **Database → Reverse Engineer…** e escolher o schema `condomanager`.

### Por linha de comandos
```bash
mysql -u root -p < condomanager_mysql.sql
mysql -u root -p < condomanager_dados_exemplo.sql
```

## Credenciais de exemplo

Criadas pelo script de dados (password BCrypt do texto `admin123`):

| Email | Password | Perfil |
|-------|----------|--------|
| `admin@condomanager.com` | `admin123` | ADMIN_SISTEMA (acesso total) |
| `gestor@silvagestao.pt` | `admin123` | GESTOR (permissões parciais) |

## Relação com a aplicação

- O esquema corresponde às entidades JPA em [`src/main/java/com/condomanager/model`](../src/main/java/com/condomanager/model).
- Em produção o perfil usa MySQL com `spring.jpa.hibernate.ddl-auto=update` (ver [`application-prod.properties`](../src/main/resources/application-prod.properties)), pelo que o Hibernate é compatível com este esquema.
- No primeiro arranque, o [`DataInitializer`](../src/main/java/com/condomanager/configuration/DataInitializer.java) cria automaticamente os perfis e o utilizador admin — estes scripts replicam esse estado para uso direto no Workbench.
