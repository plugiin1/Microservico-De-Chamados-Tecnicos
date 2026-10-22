# Estrutura do sistema

## Módulos

O projeto é dividido em três módulos Maven:

| Módulo | O que faz |
|---|---|
| `auth-core` | Usuários, perfis, senha BCrypt e a busca do usuário no login. Compartilhado pelos dois apps |
| `thindesk-web` | App web (porta 8080): login, cadastro, painel do administrador e temas |
| `chamados-service` | Microserviço REST (porta 8081): abertura, consulta e resolução de chamados |

Os dois apps usam o mesmo MongoDB Atlas e a mesma coleção `usuarios`. Por isso, um usuário criado no app web já consegue usar a API.

## Como a autenticação funciona no microserviço

1. O Postman envia usuário e senha em cada requisição (HTTP Basic).
2. O Spring Security busca o usuário no MongoDB com o `UsuarioDetailsService` do `auth-core`, o mesmo do login web.
3. A senha é comparada com o hash BCrypt salvo no banco.
4. Se o usuário existir e estiver ativo, a requisição segue com o perfil dele (`ADMIN`, `TECNICO` ou `CLIENTE`).
5. Nenhuma sessão é criada: cada requisição é autenticada de forma independente.

## Ciclo de vida do chamado

1. **Abertura** (`POST /api/chamados`): o usuário informa título e descrição. O sistema registra a solicitação, a data de abertura e o status `ABERTO`.
2. **Acompanhamento** (`GET /api/chamados/usuario/{id}` e `GET /api/chamados/{id}`): o cliente vê os próprios chamados; técnicos e administradores veem todos.
3. **Resolução** (`PUT /api/chamados/{id}/resolver`): um técnico registra a solução. O sistema muda o status para `RESOLVIDO` e registra o técnico e a data de resolução.
4. Depois de resolvido, o chamado **não pode mais ser alterado**.

## Principais decisões

- **Microserviço separado do app web**, para que a API de chamados possa evoluir e ser publicada de forma independente.
- **Autenticação compartilhada pelo módulo `auth-core`**, em vez de copiar o código de login.
- **HTTP Basic e sem sessão** na API: simples de usar no Postman e não depende de cookies, por isso não precisa de proteção CSRF.
- **DTOs de entrada só com os campos permitidos** (`titulo`, `descricao`, `solucao`): o usuário não consegue enviar o status, o solicitante ou as datas.
- **Solicitante e técnico vêm sempre do usuário autenticado**, nunca do corpo da requisição.
- **Resolução bloqueada para quem não é técnico** na regra de URL e no `@PreAuthorize`.
- **Controle de concorrência com `@Version`**: se dois técnicos resolverem o mesmo chamado ao mesmo tempo, só o primeiro é gravado.
- **Erros padronizados em JSON** (`ApiExceptionHandler`), com o status HTTP adequado: 400, 401, 403, 404 e 409.

