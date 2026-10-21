package com.qa.automation.web;

import com.qa.automation.web.pages.LoginPage;
import com.qa.automation.web.pages.ProductsPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

// Asserts: comparam o resultado OBTIDO com o ESPERADO.
// Se forem diferentes, o teste falha.
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ============================================================================
 * TESTES WEB - Login no Sauce Demo (Selenium + Page Object)
 * ============================================================================
 *
 * COMO O SELENIUM FUNCIONA:
 *  Teste (Java) -> WebDriver -> ChromeDriver -> navegador Chrome
 *  O Selenium envia comandos ("abra esta URL", "clique aqui") através do
 *  protocolo W3C WebDriver. Desde o Selenium 4.6, o "Selenium Manager"
 *  baixa o ChromeDriver automaticamente: só precisa ter o Chrome instalado.
 *
 * MODO HEADLESS:
 *  Por padrão o Chrome roda "headless" (sem janela), que é mais rápido e
 *  é como roda no pipeline (CI). Para VER o navegador abrindo, rode:
 *      mvn test -Dgroups=web -Dheadless=false
 *
 * BOA PRÁTICA - TESTES INDEPENDENTES:
 *  Cada teste abre o PRÓPRIO navegador (@BeforeEach) e fecha ao terminar
 *  (@AfterEach). Assim, um teste não interfere no outro e eles podem rodar
 *  em qualquer ordem, ou até em paralelo.
 *
 * ESTRUTURA DE CADA TESTE - padrão AAA:
 *  Arrange (preparar) -> Act (agir) -> Assert (verificar)
 * ============================================================================
 */
@Tag("web")
@DisplayName("Login Web - Sauce Demo")
class LoginWebTest {

    private WebDriver driver;
    private LoginPage loginPage;

    /**
     * @BeforeEach: roda ANTES DE CADA teste.
     * Abre o navegador e já entra na tela de login.
     */
    @BeforeEach
    void abrirNavegador() {
        ChromeOptions opcoes = new ChromeOptions();

        // Lê a propriedade "headless" passada no comando (-Dheadless=false).
        // Se não for passada, roda sem janela.
        if (!"false".equals(System.getProperty("headless"))) {
            opcoes.addArguments("--headless=new");
        }
        // Tamanho fixo de janela: evita que o layout mude (versão mobile do
        // site) e esconda elementos, o que deixaria o teste instável.
        opcoes.addArguments("--window-size=1366,768");

        driver = new ChromeDriver(opcoes);
        loginPage = new LoginPage(driver).abrir();
    }

    /**
     * @AfterEach: roda DEPOIS DE CADA teste, mesmo se ele falhar.
     * Fecha o navegador para não deixar processos abertos na máquina.
     * quit() fecha o navegador inteiro; close() fecharia só a aba atual.
     */
    @AfterEach
    void fecharNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ------------------------------------------------------------------------
    // CAMINHO FELIZ
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("Login com credenciais válidas leva à tela de produtos")
    void deveLogarComSucesso() {
        // Act (agir): faz login com usuário e senha válidos
        ProductsPage produtos = loginPage.loginComSucesso("standard_user", "secret_sauce");

        // Assert (verificar): assertEquals(ESPERADO, OBTIDO)
        assertEquals("Products", produtos.obterTitulo());
    }

    @Test
    @DisplayName("Adicionar produto atualiza o contador do carrinho")
    void deveAdicionarProdutoAoCarrinho() {
        // Encadeamento de métodos: possível porque cada ação do Page Object
        // retorna uma página. O teste lê como uma frase.
        String quantidade = loginPage
                .loginComSucesso("standard_user", "secret_sauce")
                .adicionarMochilaAoCarrinho()
                .obterQuantidadeNoCarrinho();

        assertEquals("1", quantidade);
    }

    // ------------------------------------------------------------------------
    // CENÁRIOS NEGATIVOS - TESTE PARAMETRIZADO (data-driven)
    // ------------------------------------------------------------------------
    /**
     * Um único teste que roda 4 vezes, uma para cada linha do @CsvSource.
     * Cada linha tem: usuário, senha, trecho da mensagem esperada.
     * '' (aspas simples vazias) representa um campo em branco.
     *
     * Técnica de teste aplicada: PARTIÇÃO DE EQUIVALÊNCIA. Cobrimos um
     * representante de cada grupo de erro: senha errada, usuário bloqueado,
     * usuário vazio e senha vazia.
     */
    @ParameterizedTest(name = "Login inválido: usuário=''{0}'' → ''{2}''")
    @CsvSource({
            "standard_user,   senha_errada, do not match",
            "locked_out_user, secret_sauce, locked out",
            "'',              secret_sauce, Username is required",
            "standard_user,   '',           Password is required"
    })
    void deveExibirErroNoLoginInvalido(String usuario, String senha, String mensagemEsperada) {
        // Act
        String erro = loginPage.loginComFalha(usuario, senha).obterMensagemErro();

        // Assert: a mensagem exibida deve CONTER o trecho esperado.
        // O texto após a vírgula é a mensagem mostrada se o teste falhar,
        // facilitando a análise do erro.
        assertTrue(erro.contains(mensagemEsperada),
                "Mensagem esperada conter: " + mensagemEsperada + " | obtida: " + erro);
    }
}
