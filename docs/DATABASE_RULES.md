# Base de Dados
Motor:
MySQL 8

ORM:
JPA + Hibernate

# Convenções
- Tabelas em snake_case
- Chaves primárias com prefixo id_
- Foreign Keys explícitas

# Obrigatório
Utilizar:
@OneToMany
@ManyToOne
@OneToOne
@ManyToMany

# Nunca escrever SQL quando existir solução JPA.
