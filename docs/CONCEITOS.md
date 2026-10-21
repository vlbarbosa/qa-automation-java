# Conceitos usados no projeto

Este documento explica a teoria por trás de cada parte do código. Cada seção indica em qual arquivo o conceito aparece.

---

## 1. Pirâmide de testes

```
        /\        UI (Web e Mobile)  → poucos: lentos e frágeis
       /  \
      /----\      API / Integração   → quantidade média
     /      \
    /--------\    Unitários          → muitos: rápidos e baratos
```

Quanto mais alto na pirâmide, mais lento e caro o teste. Por isso este projeto tem **mais testes de API** do que de tela: regras de negócio se validam na API, e a UI fica para os fluxos críticos (como o login).

---

## 2. Testes de API — `api/PostsApiTest.java`

### Métodos HTTP

| Método | Para quê | Idempotente? |
| --- | --- | --- |
| GET | Consultar | Sim |
| POST | Criar | Não |
| PUT | Substituir o registro inteiro | Sim |
| PATCH | Alterar parte do registro | Não necessariamente |
| DELETE | Excluir | Sim |

**Idempotente:** chamar uma ou várias vezes produz o mesmo resultado.

### Status codes

| Código | Significado |
| --- | --- |
| 200 | OK |
| 201 | Criado |
| 204 | Sucesso sem corpo |
| 400 | Requisição inválida |
| 401 | Não autenticado ("não sei quem você é") |
| 403 | Sem permissão ("sei quem é, mas não pode") |
| 404 | Não encontrado |
| 422 | Regra de negócio violada |
| 500 | Erro interno do servidor |

### given / when / then

```java
given()   // DADO: preparo (headers, body, parâmetros)
.when()   // QUANDO: a chamada HTTP
.then()   // ENTÃO: as validações
```

### O que validar numa API

1. Status code
2. Corpo da resposta (campos e valores)
3. Contrato (formato e tipos) — arquivo `schemas/post-schema.json`
4. Tempo de resposta
5. Cenários negativos (dados inválidos, recurso inexistente)
6. Autenticação e permissão
7. Persistência (criou? consulte e confira)

### Teste de contrato

O JSON Schema define quais campos são obrigatórios e de que tipo. Se o back-end mudar o formato da resposta, quem consome a API (app, site, outro sistema) pode quebrar. O teste de contrato detecta isso antes.

---

## 3. Testes Web — `web/`

### Selenium WebDriver

```
Teste Java → WebDriver → ChromeDriver → Google Chrome
```

O Selenium controla o navegador pelo protocolo **W3C WebDriver**. O **Selenium Manager** (desde a versão 4.6) baixa o ChromeDriver sozinho.

### Locators (ordem de preferência)

| Locator | Exemplo | Comentário |
| --- | --- | --- |
| id | `By.id("login-button")` | Mais rápido e estável |
| name | `By.name("email")` | Bom quando não há id |
| cssSelector | `By.cssSelector("[data-test='error']")` | Flexível e rápido |
| xpath | `By.xpath("//button[text()='Entrar']")` | Poderoso, mas frágil e lento |

Nunca usar XPath absoluto (`/html/body/div[2]/...`): qualquer mudança no layout quebra o teste.

### Esperas

| Tipo | Como funciona | Usar? |
| --- | --- | --- |
| Implícita | Vale para todo `findElement` | Evitar misturar com explícita |
| **Explícita** (`WebDriverWait`) | Espera uma condição num elemento | **Sim, a recomendada** |
| Fluent | Explícita com intervalo de checagem configurável | Casos específicos |
| `Thread.sleep` | Pausa fixa | **Nunca** |

No projeto: `BasePage.java`, métodos `esperarVisivel` e `clicar`.

### Page Object Model (POM)

Cada tela vira uma classe com seus locators (privados) e ações (públicas).

| Sem POM | Com POM |
| --- | --- |
| Locators repetidos em vários testes | Locator definido uma vez |
| Mudou a tela → corrige vários arquivos | Mudou a tela → corrige uma classe |
| Teste cheio de `findElement` | Teste legível: `loginPage.loginComSucesso(...)` |

### Teste "flaky" (instável)

Passa e falha sem mudança no código. Causas comuns e como o projeto evita:

| Causa | Prevenção no projeto |
| --- | --- |
| Esperas mal feitas | Esperas explícitas na `BasePage` |
| Testes dependentes entre si | Cada teste abre e fecha seu navegador |
| Layout variando com o tamanho da tela | Janela fixa 1366x768 |
| Locators frágeis | id e `data-test` |

---

## 4. Orientação a Objetos no projeto

| Pilar | Onde aparece |
| --- | --- |
| **Encapsulamento** | Locators `private` nas Page Objects |
| **Herança** | `LoginPage extends BasePage` |
| **Abstração** | `BasePage` é `abstract`; o teste chama `login()` sem saber os cliques |
| **Polimorfismo** | `LoginScreen` recebe `AppiumDriver`, que pode ser `AndroidDriver` ou `IOSDriver` |

---

## 5. Testes Mobile — `mobile/`

### Arquitetura do Appium

```
Teste Java ──HTTP──> Servidor Appium ──> Driver ──> Celular / Emulador
(cliente)            (Node.js :4723)     UiAutomator2 (Android)
                                         XCUITest (iOS)
```

### Appium 2

- Drivers instalados separadamente: `appium driver install uiautomator2`
- Capabilities não padrão usam o prefixo `appium:`
- Sistema de plugins

### Capabilities principais

| Capability | Para quê |
| --- | --- |
| platformName | Android ou iOS |
| automationName | UiAutomator2 ou XCUITest |
| deviceName / udid | Qual dispositivo |
| app | Caminho do .apk / .ipa |
| appPackage + appActivity | Abrir app já instalado (Android) |
| noReset | Manter dados do app entre sessões |

### Tipos de app

| Tipo | O que é |
| --- | --- |
| Nativo | Feito para a plataforma |
| Web mobile | Site no navegador do celular |
| Híbrido | App com WebView; troca de contexto com `driver.context(...)` |

### Desafios do teste mobile

Fragmentação de aparelhos e versões, permissões, interrupções (ligação, notificação), rede instável, rotação de tela, teclado cobrindo campos, app em segundo plano.

---

## 6. JUnit 5 — anotações usadas

| Anotação | Função |
| --- | --- |
| `@Test` | Marca um teste |
| `@BeforeAll` | Roda uma vez antes de todos (método `static`) |
| `@BeforeEach` | Roda antes de cada teste |
| `@AfterEach` | Roda depois de cada teste |
| `@DisplayName` | Nome legível no relatório |
| `@Tag` | Agrupa testes (api, web, mobile) |
| `@ParameterizedTest` | Mesmo teste com vários dados |
| `@ValueSource` / `@CsvSource` | Fornecem os dados do teste parametrizado |

---

## 7. Técnicas de teste aplicadas

| Técnica | Onde |
| --- | --- |
| Partição de equivalência | Login inválido: um representante de cada tipo de erro |
| Cenários negativos | 404 na API; erros de login na Web |
| Data-driven | `@CsvSource` e `@ValueSource` |
| Teste de contrato | JSON Schema |
| Padrão AAA (Arrange, Act, Assert) | Estrutura dos testes Web |

---

## 8. BDD e CI

- **BDD:** cenários em Gherkin (`features/login.feature`) descrevem o comportamento em linguagem de negócio. Com Cucumber, cada frase vira um método Java.
- **CI:** o GitHub Actions (`.github/workflows/tests.yml`) roda os testes a cada push e guarda os relatórios.
