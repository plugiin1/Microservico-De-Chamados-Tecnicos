# Changelog

Todas as mudanças relevantes do ThinDesk são registradas aqui.

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/) e o projeto adota o
[Versionamento Semântico 2.0.0](https://semver.org/lang/pt-BR/) — veja a explicação do formato no
[README](README.md#versionamento).

## [1.1.0-beta] — 2026-11-24

### Adicionado

- Dashboard de chamados no app web (`/dashboard`), com indicadores de total, abertos, resolvidos,
  percentual de resolução e tempo médio de atendimento, gráficos dos últimos 7 dias e por situação,
  chamados recentes e ranking de técnicos.
- Telas de chamados no app web: listagem com filtro por status, abertura e resolução.
- Endpoint `GET /api/chamados` no microserviço, com filtro opcional por status, restrito a
  técnicos e administradores.
- Endpoint público `GET /api/info` com nome do serviço, versão e status, usado como health check.
- Exibição da versão do sistema no menu lateral do app web.
- Deploy em containers no Render: `Dockerfile.web`, `Dockerfile.chamados`, `render.yaml` e
  `.dockerignore`.
- Suporte à porta definida pela hospedagem (`PORT`), cabeçalhos de proxy no app web e cache de
  templates e nível de log configuráveis por variável de ambiente.
- Tolerância à hibernação do plano gratuito: as consultas ao microserviço são repetidas enquanto
  ele estiver subindo (`502`, `503` e `504`) e um ping periódico opcional
  (`CHAMADOS_API_KEEP_ALIVE`) mantém o serviço acordado.

### Alterado

- A rota inicial do app web (`/`) passa a redirecionar para o dashboard.
- O app web consome o microserviço por HTTP (HTTP Basic com as credenciais do usuário logado),
  em vez de ler a coleção de chamados diretamente.

## [1.0.0-alpha] — 2026-10-22

### Adicionado

- Microserviço `chamados-service` com autenticação HTTP Basic reaproveitando o `auth-core`.
- Abertura de chamados (`POST /api/chamados`), consulta por usuário e por id, e resolução por
  técnicos (`PUT /api/chamados/{id}/resolver`) com bloqueio de alteração após resolvido.
- Controle de concorrência otimista na resolução e erros padronizados em JSON.
- Coleção do Postman com testes automáticos e documentação do projeto.

### Alterado

- Projeto separado nos módulos `auth-core`, `thindesk-web` e `chamados-service`.
- Módulo de chamados legado do app web removido em favor do microserviço.

[1.1.0-beta]: https://github.com/plugiin1/Microservico-De-Chamados-Tecnicos/releases/tag/v1.1.0-beta
[1.0.0-alpha]: https://github.com/plugiin1/Microservico-De-Chamados-Tecnicos/releases/tag/v1.0.0-alpha
