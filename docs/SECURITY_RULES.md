# Segurança
Obrigatório:
- Spring Security
- BCrypt
- JWT

# Passwords
Nunca guardar passwords em texto simples.
Utilizar BCrypt.

# Autorização
Role Based Access Control (RBAC)

Os perfis são os definidos em DATABASE_SCHEMA.md / DOMAIN_MODEL.md (em PT).
A autoridade Spring Security é ROLE_<nome>:

| Perfil (perfil.nome) | Autoridade Spring |
|----------------------|-------------------|
| ADMIN_SISTEMA        | ROLE_ADMIN_SISTEMA |
| GESTOR_EMPRESA       | ROLE_GESTOR_EMPRESA |
| FUNCIONARIO          | ROLE_FUNCIONARIO |
| ADMIN_CONDOMINIO     | ROLE_ADMIN_CONDOMINIO |
| CONDOMINO            | ROLE_CONDOMINO |

# Auditoria
Registar:
- Login
- Logout
- Criação
- Alteração
- Eliminação
