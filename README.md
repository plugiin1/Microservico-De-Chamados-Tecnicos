# Sistema de Login Seguro

Sistema de autenticação e autorização desenvolvido com **Java Spring Boot**, **Spring Security**, **Thymeleaf** e **MongoDB Atlas**. Ele foi construído de forma genérica, para servir de base ao PFC.

![Tela inicial do sistema com o tema Verde-Menta, logado como administrador](docs/images/tela-inicial.png)

## Funcionalidades

- Cadastro de usuários com validação de dados e senha criptografada com **BCrypt**
- Login e logout com **Spring Security**, com proteção **CSRF** em todos os formulários
- Controle de acesso por **3 perfis**: `ADMIN`, `TECNICO` e `CLIENTE`
- **Sessões armazenadas no MongoDB Atlas** (Spring Session): a sessão sobrevive a reinícios do servidor e permite rodar várias instâncias
- **Painel administrativo**: listar usuários, trocar perfil, ativar/desativar e excluir contas, encerrando as sessões do usuário afetado na hora
- **Layouts reutilizáveis** com Thymeleaf Layout Dialect
- **Temas visuais configuráveis** (Padrão, Oceano, Verde-Menta, Escuro), escolhidos pelo usuário e salvos em cookie

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 21+ |
| Spring Boot | 3.5.6 |
| Spring Security | 6.5 (gerenciado pelo Boot) |
| Spring Data MongoDB / Spring Session MongoDB | gerenciados pelo Boot |
| Thymeleaf + Layout Dialect + Extras Spring Security | 3.1 / 3.4.0 / 6 |
| Bootstrap / Bootstrap Icons | 5.3.3 / 1.11.3 (via CDN) |
| Lombok | gerenciado pelo Boot |
| MongoDB Atlas | cluster (gratuito) |

## Pré-requisitos

- **JDK 21 ou superior** instalado (`java -version`)
- Uma conta no **[MongoDB Atlas](https://www.mongodb.com/cloud/atlas)**

O Maven **não** precisa estar instalado, porque o projeto inclui o Maven Wrapper (`mvnw` / `mvnw.cmd`).

## Configurando o MongoDB Atlas

1. **Crie um cluster**: no Atlas, clique em *Create* e escolha o plano **M0 (Free)**.
2. **Crie um usuário de banco**: em *Security → Database Access*, clique em *Add New Database User*. Defina usuário e senha. O mais seguro é dar a ele apenas o papel **readWrite** no banco da aplicação (ex.: `thindesk`).
3. **Libere seu IP**: em *Security → Network Access*, clique em *Add IP Address* e depois em *Add Current IP Address*. Só os IPs dessa lista conseguem se conectar.
4. **Copie a string de conexão**: no cluster, clique em *Connect → Drivers → Java* e copie a URI:
   ```
   mongodb+srv://<usuario>:<db_password>@<cluster>.xxxxx.mongodb.net/?appName=<Cluster>
   ```
5. Troque `<db_password>` pela senha do usuário, **sem** os sinais `<` e `>`. Caracteres especiais na senha precisam ser codificados (`@` → `%40`, `:` → `%3A`, `/` → `%2F`, `#` → `%23`).

O banco e as coleções são criados automaticamente na primeira execução. Não é preciso criar nada manualmente.

## Configurando o ambiente

As credenciais **nunca** ficam no código. A aplicação lê as configurações de variáveis de ambiente ou de um arquivo **`.env`** na raiz do projeto. Esse arquivo está no `.gitignore` e não vai para o GitHub.

1. Copie o modelo:
   ```bash
   # Windows (PowerShell)
   Copy-Item .env.example .env
   # Linux / macOS
   cp .env.example .env
   ```
2. Edite o `.env`:

| Variável | Obrigatória | Descrição | Padrão |
|---|:-:|---|---|
| `MONGODB_URI` | ✅ | String de conexão do Atlas | — |
| `MONGODB_DATABASE` | | Nome do banco | `thindesk` |
| `ADMIN_USERNAME` | | Usuário do administrador inicial | `admin` |
| `ADMIN_EMAIL` | | E-mail do administrador inicial | `admin@thindesk.local` |
| `ADMIN_PASSWORD` | ✅ | Senha do administrador inicial | — |
| `SESSION_TIMEOUT` | | Tempo de inatividade até a sessão expirar | `30m` |
| `COOKIE_SECURE` | | `true` para enviar o cookie de sessão só por HTTPS (use em produção) | `false` |
| `APP_NOME` | | Nome exibido no sistema | `Thindesk` |
| `APP_TEMA` | | Tema padrão (`padrao`, `oceano`, `menta`, `escuro`) | `padrao` |

¹ Obrigatória apenas na primeira execução, quando ainda não existe nenhum administrador no banco. Se não for definida, o administrador não é criado e um aviso aparece no log.

Também é possível definir essas variáveis direto no sistema operacional ou na configuração de execução da IDE, em vez de usar o `.env`.

## Executando

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Acesse **http://localhost:8080** e entre com o `ADMIN_USERNAME` / `ADMIN_PASSWORD` definidos no `.env`.

Para gerar o `.jar` e executar:

```bash
.\mvnw.cmd clean package
java -jar target/thindesk-1.0-alpha.jar
```

Execute o `java -jar` a partir da pasta onde está o `.env`, porque ele é procurado no diretório atual.

Também dá para rodar pela IDE (IntelliJ, NetBeans, VS Code), executando a classe `com.pfc.ThindeskApplication`.

## Perfis

- Todo usuário que se cadastra pela tela pública recebe o perfil **CLIENTE**. O perfil nunca vem do formulário.
- O **ADMIN** promove usuários a TECNICO ou ADMIN pelo painel `/admin/usuarios`.
- O primeiro ADMIN é criado automaticamente a partir do `.env`.
- O menu lateral mostra apenas os itens liberados para o perfil logado.

## Integração com o MongoDB Atlas

| Coleção | Conteúdo |
|---|---|
| `usuarios` | Usuários (nome, username e e-mail com **índice único**, hash BCrypt da senha, perfil, ativo, data de cadastro) |
| `sessoes` | Sessões HTTP do Spring Session. Um **índice TTL** remove automaticamente as sessões expiradas |
| `chamados`, `clientes`, `horariosAtendimento` | Domínio de exemplo (Thindesk) |

**Conexão segura:**
- O protocolo `mongodb+srv://` usa **TLS** por padrão. O Atlas não aceita conexões sem criptografia.
- O usuário e a senha ficam apenas no `.env` ou em variáveis de ambiente, nunca no repositório.
- Só os IPs cadastrados no *Network Access* do Atlas conseguem se conectar.
- O recomendado é um usuário de banco com o mínimo de privilégios necessário (`readWrite` no banco da aplicação).

**Como a aplicação se conecta:**
- `spring.config.import=optional:file:.env[.properties]` carrega o `.env`.
- `spring.data.mongodb.uri=${MONGODB_URI}` passa a URI para o driver oficial do MongoDB.
- `spring.data.mongodb.auto-index-creation=true` cria os índices únicos declarados com `@Indexed`.
- O `spring-session-data-mongodb` é configurado automaticamente pelo Spring Boot e grava as sessões na coleção `sessoes`.

## Medidas de segurança

| Medida | Onde |
|---|---|
| Hash de senha com **BCrypt** (custo 12) | `PasswordConfig` |
| Validação de dados (tamanho, formato, e-mail, força da senha, confirmação) | `CadastroForm` |
| Username e e-mail únicos (verificação + índice único no banco) | `UsuarioService`, `Usuario` |
| Proteção **CSRF** em todos os formulários | `SecurityConfig` (padrão do Spring Security) |
| Logout só por POST, invalidando a sessão e apagando os cookies | `SecurityConfig` |
| Troca do ID da sessão no login (proteção contra *session fixation*) | `SecurityConfig` |
| Cookie de sessão `HttpOnly` e `SameSite=Lax` (e `Secure` em produção) | `application.properties` |
| Autorização por URL **e** por método (`@PreAuthorize`) no painel admin | `SecurityConfig`, `AdminUsuarioController` |
| Perfil nunca aceito do formulário público | `UsuarioService` |
| Usuário desativado não consegue logar e tem as sessões encerradas | `UsuarioDetailsService`, `SessaoService` |
| Admin não pode rebaixar, desativar ou excluir a si mesmo, nem deixar o sistema sem admin | `AdminUsuarioService` |
| Mensagem de erro de login genérica (não revela se o usuário existe) | `login.html` |
| Proteção contra *open redirect* na troca de tema | `TemaController` |
| Credenciais fora do código, em `.env` (ignorado pelo Git) | `.env.example`, `.gitignore` |

## Temas e personalização

- **Trocar o nome e a logo**: altere `APP_NOME` no `.env` e substitua `src/main/resources/static/images/logo.png`.
- **Trocar o tema padrão**: altere `APP_TEMA`.
- **Criar um tema novo** (ex.: `roxo`):
  1. Crie `src/main/resources/static/themes/roxo/theme.css`, copiando um tema existente e mudando as cores.
  2. Adicione no `application.properties`:
     ```properties
     app.ui.temas.roxo.descricao=Roxo
     app.ui.temas.roxo.modo=light
     ```
  O tema aparece no seletor do menu sem alterar Java nem HTML. Use `modo=dark` para ativar o modo escuro do Bootstrap.

## Estrutura do projeto

```
src/main/java/com/pfc
├── ThindeskApplication.java      # classe principal
├── MongoInitConfig.java          # cria as coleções na inicialização
├── auth/                         # módulo de autenticação (genérico, reutilizável)
│   ├── config/                   # SecurityConfig, PasswordConfig, AdminInitializer
│   ├── controller/               # login, cadastro, painel admin
│   ├── dto/                      # CadastroForm (validação)
│   ├── entity/                   # Usuario, Role
│   ├── exception/                # exceções de regra de negócio
│   ├── repository/               # UsuarioRepository
│   └── service/                  # UsuarioService, UsuarioDetailsService, AdminUsuarioService, SessaoService
├── ui/                           # módulo de interface: nome, logo e temas
└── thindesk/                     # domínio de exemplo (adaptações futuras)

src/main/resources
├── application.properties
├── static/
│   ├── css/style.css             # estilos base (variáveis CSS)
│   ├── js/script.js
│   ├── images/logo.png
│   └── themes/<tema>/theme.css   # um arquivo por tema
└── templates/
    ├── layouts/base.html         # layout das páginas internas (menu lateral)
    ├── layouts/auth.html         # layout de login, cadastro e erros
    ├── fragments/sidebar.html
    ├── auth/                     # login, cadastro, acesso negado
    ├── admin/                    # painel de usuários
    └── ...                       # páginas do domínio
```

A estrutura do sistema, as principais decisões de design e como adaptá-lo ao PFC estão em **[docs/ESTRUTURA.md](docs/ESTRUTURA.md)**.

