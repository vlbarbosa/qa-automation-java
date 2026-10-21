# QA Automation Java

Projeto de automação de testes em Java cobrindo as três camadas de uma aplicação: **API**, **Web** e **Mobile**.

O código está todo comentado em português, explicando cada conceito e cada decisão. A pasta [`docs`](docs) traz a teoria por trás do projeto.

## Tecnologias

| Camada | Ferramentas |
| --- | --- |
| Linguagem e build | Java 25 LTS, Maven |
| Execução dos testes | JUnit 5 (tags, testes parametrizados) |
| API | RestAssured, JSON Schema Validator |
| Web | Selenium WebDriver 4, Page Object Model, esperas explícitas |
| Mobile | Appium 2 (java-client), UiAutomator2, Page Factory para Android e iOS |
| BDD | Cenários em Gherkin |
| CI | GitHub Actions |

## Estrutura do projeto

```
qa-automation-java
├── pom.xml                         → configuração do Maven e dependências
├── .github/workflows/tests.yml     → pipeline que roda os testes no GitHub
├── apps/                           → onde fica o .apk do teste mobile
├── docs/
│   ├── CONCEITOS.md                → teoria: testes, POM, esperas, Appium, API
│   ├── INSTALACAO.md               → o que instalar e como rodar no VS Code
│   └── ROTEIRO-ENTREVISTA.md       → como apresentar este projeto
└── src/test
    ├── java/com/qa/automation
    │   ├── api/
    │   │   └── PostsApiTest.java   → GET, POST, PUT, DELETE, contrato, negativos
    │   ├── web/
    │   │   ├── LoginWebTest.java   → testes de login e carrinho
    │   │   └── pages/              → Page Objects
    │   │       ├── BasePage.java
    │   │       ├── LoginPage.java
    │   │       └── ProductsPage.java
    │   └── mobile/
    │       ├── LoginMobileTest.java → teste Appium no Android
    │       └── pages/
    │           └── LoginScreen.java → Page Object Android + iOS
    └── resources
        ├── schemas/post-schema.json → contrato da API
        └── features/login.feature   → cenários BDD
```

## Sistemas testados

| Camada | Sistema | Observação |
| --- | --- | --- |
| API | [JSONPlaceholder](https://jsonplaceholder.typicode.com) | API pública gratuita para estudos |
| Web | [Sauce Demo](https://www.saucedemo.com) | Loja de demonstração para automação |
| Mobile | [My Demo App](https://github.com/saucelabs/my-demo-app-android) | App Android de demonstração da Sauce Labs |

## Cenários automatizados

**API (`PostsApiTest`)**

- Consultar post por id e validar campos e tempo de resposta
- Validar o contrato da resposta com JSON Schema
- Listar todos os posts
- Filtrar posts por usuário (query param)
- Criar, atualizar e excluir post
- Cenário negativo: post inexistente retorna 404 (teste parametrizado)
- Encadeamento: buscar um post e depois os comentários dele

**Web (`LoginWebTest`)**

- Login com sucesso leva à tela de produtos
- Adicionar produto atualiza o carrinho
- Login inválido em 4 situações: senha errada, usuário bloqueado, usuário vazio, senha vazia (teste parametrizado)

**Mobile (`LoginMobileTest`)**

- Login no app Android (exemplo de estrutura; exige ambiente Appium)

## Como executar

Pré-requisitos: Java 25+ e Chrome. Passo a passo completo em [docs/INSTALACAO.md](docs/INSTALACAO.md).

**Pelo VS Code:** abra a pasta do projeto, clique no ícone de Testing (béquer) e rode a classe desejada.

**Pelo terminal (com Maven instalado):**

```bash
# API + Web (padrão)
mvn test

# Somente API
mvn test -Dgroups=api

# Somente Web, vendo o navegador abrir
mvn test -Dgroups=web -Dheadless=false

# Mobile (Appium rodando + emulador aberto)
mvn test -Dgroups=mobile -Dapp=C:/caminho/app.apk
```

## Boas práticas aplicadas

- **Page Object Model** com classe base (herança) e locators encapsulados
- **Esperas explícitas** (`WebDriverWait`), sem `Thread.sleep`
- **Testes independentes**: cada um abre e fecha a própria sessão
- **Caminho feliz e cenários negativos**, com testes parametrizados (data-driven)
- **Teste de contrato** da API com JSON Schema
- **Tags** para rodar só a camada desejada
- **Pipeline de CI** rodando os testes a cada push
- **Page Object mobile multiplataforma**: um código para Android e iOS

## Próximos passos

- Executar os cenários Gherkin com Cucumber
- Relatórios visuais com Allure
- Execução paralela e em device farm (BrowserStack / Sauce Labs)
- Variáveis de ambiente (dev, homologação) em arquivo de configuração
