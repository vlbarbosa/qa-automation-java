package com.qa.automation.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * ============================================================================
 * LOGIN PAGE - Page Object da tela de login
 * ============================================================================
 *
 * O QUE É PAGE OBJECT MODEL (POM)?
 *  É um padrão de projeto em que CADA TELA do sistema vira UMA CLASSE.
 *  A classe guarda:
 *    1. Os LOCATORS (como encontrar cada elemento da tela);
 *    2. As AÇÕES que o usuário faz na tela (fazer login, ler erro...).
 *
 * POR QUE USAR?
 *  - MANUTENÇÃO: se o id de um botão mudar, corrigimos em UM lugar só,
 *    e não em todos os testes que usam esse botão.
 *  - REAPROVEITAMENTO: vários testes usam o mesmo método login().
 *  - LEGIBILIDADE: o teste fica parecido com linguagem natural.
 *
 * SISTEMA TESTADO: Sauce Demo (https://www.saucedemo.com),
 * uma loja de demonstração criada para praticar automação.
 *
 * LOCATORS - ordem de preferência:
 *   id  >  name  >  cssSelector  >  xpath
 *  O id é o mais rápido e estável. O XPath é poderoso, mas mais frágil
 *  e lento; usar só quando não houver alternativa.
 * ============================================================================
 */
public class LoginPage extends BasePage { // "extends" = herda da BasePage

    // Endereço da página. "static final" = constante da classe.
    private static final String URL = "https://www.saucedemo.com/";

    // ---------------------- LOCATORS ----------------------
    // São "private" (ENCAPSULAMENTO): o teste não precisa saber como
    // os elementos são encontrados; ele só chama as ações públicas.
    private final By campoUsuario = By.id("user-name");
    private final By campoSenha   = By.id("password");
    private final By botaoLogin   = By.id("login-button");
    // Exemplo de CSS Selector por atributo: [atributo='valor']
    private final By mensagemErro = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        // "super" chama o construtor da classe mãe (BasePage).
        super(driver);
    }

    // ---------------------- AÇÕES ----------------------

    /**
     * Abre a página de login no navegador.
     * Retorna "this" (a própria página) para permitir encadear chamadas:
     * new LoginPage(driver).abrir().loginComFalha(...)
     */
    public LoginPage abrir() {
        driver.get(URL);
        return this;
    }

    /**
     * Faz login com dados válidos.
     * Retorna a PRÓXIMA página (ProductsPage), porque após o login o
     * usuário é levado para a tela de produtos. Isso deixa claro no código
     * qual é o fluxo de navegação do sistema.
     */
    public ProductsPage loginComSucesso(String usuario, String senha) {
        preencherLogin(usuario, senha);
        return new ProductsPage(driver);
    }

    /**
     * Faz login com dados inválidos.
     * Retorna a MESMA página, pois o usuário continua na tela de login
     * vendo a mensagem de erro.
     */
    public LoginPage loginComFalha(String usuario, String senha) {
        preencherLogin(usuario, senha);
        return this;
    }

    /**
     * Lê o texto da mensagem de erro exibida na tela.
     */
    public String obterMensagemErro() {
        return esperarVisivel(mensagemErro).getText();
    }

    /**
     * Método PRIVADO de apoio: só esta classe usa.
     * Concentra o preenchimento para não repetir o código nos dois logins.
     */
    private void preencherLogin(String usuario, String senha) {
        escrever(campoUsuario, usuario); // método herdado da BasePage
        escrever(campoSenha, senha);
        clicar(botaoLogin);
    }
}
