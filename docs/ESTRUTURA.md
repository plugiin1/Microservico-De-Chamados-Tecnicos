# Estrutura do sistema

## Módulos

O código fica em `src/main/java/com/pfc` e é dividido em três partes:

| Pacote | O que faz |
|---|---|
| `auth` | Login, cadastro, usuários, perfis, sessões e painel do administrador |
| `ui` | Nome do sistema, logo e temas visuais |
| `thindesk` | Exemplo de domínio (chamados e clientes), que pode ser trocado pelo tema do PFC |

O módulo `auth` não depende do `thindesk`, então pode ser reaproveitado em outro projeto.

## Como o login funciona

1. O usuário preenche o login.
2. O Spring Security busca o usuário no MongoDB (`UsuarioDetailsService`).
3. A senha digitada é comparada com o hash BCrypt salvo no banco.
4. Se estiver tudo certo, a sessão é criada e salva na coleção `sessoes` do MongoDB Atlas.
5. A cada página acessada, o Spring Security verifica se o perfil do usuário tem permissão.

## Principais decisões

- **Três perfis (`ADMIN`, `TECNICO`, `CLIENTE`)** definidos no enum `Role`.
- **O cadastro público sempre cria CLIENTE.** Só o administrador pode promover usuários, pelo painel.
- **Senhas com BCrypt**, nunca salvas em texto puro.
- **Formulário separado da entidade** (`CadastroForm`), para o usuário não conseguir enviar campos como o perfil.
- **Sessões no MongoDB** (Spring Session): o servidor pode reiniciar sem deslogar ninguém, e o admin consegue encerrar a sessão de um usuário.
- **Regras de acesso em um só lugar**: `SecurityConfig`.
- **Credenciais fora do código**, no arquivo `.env`.
- **Layouts reutilizáveis** (`layouts/base.html` e `layouts/auth.html`): as páginas só definem o próprio conteúdo.
- **Temas com variáveis CSS**: cada tema é um arquivo `theme.css` em `static/themes/`.

## Como adaptar para o PFC

- **Nome e logo:** altere `APP_NOME` no `.env` e substitua `static/images/logo.png`.
- **Novo tema:** crie `static/themes/<nome>/theme.css` e adicione duas linhas em `application.properties` (veja o README).
- **Novo domínio:** substitua o pacote `thindesk` e as páginas dele. 
- **Novas rotas:** adicione-as nas listas de rotas do `SecurityConfig` e o link em `fragments/sidebar.html`.
- **Novo perfil:** adicione um valor no enum `Role` e ajuste o `SecurityConfig`.
