# SaaS

Multi Tenant
Cada empresa de gestão de condomínios é um Tenant, ou seja pode ter vários condomínios como clientes.

# Isolamento
Um tenant nunca pode visualizar dados de outro tenant. Uma empresa de gestão de condomínios nunca pode aceder aos dados de outra.

# Todas as entidades devem possuir:
id_empresa_condominio

Exemplos
empresa_gestao
condominio
fracao
condomino

# Filtros
Todas as queries devem respeitar tenant_id.
