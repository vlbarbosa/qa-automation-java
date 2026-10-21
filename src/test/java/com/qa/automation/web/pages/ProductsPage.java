package com.qa.automation.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * ============================================================================
 * PRODUCTS PAGE - Page Object da tela de produtos
 * ============================================================================
 * Tela que aparece depois do login bem-sucedido no Sauce Demo.
 * Segue o mesmo padrão da LoginPage: locators privados + ações públicas.
 * ============================================================================
 */
public class ProductsPage extends BasePage {

    // ---------------------- LOCATORS ----------------------
    // O atributo "data-test" é colocado pelos devs especialmente para
    // automação. É o locator ideal: não muda quando o layout muda.
    // Dica de entrevista: QA pode pedir ao time para incluir esses atributos.
    private final By titulo                = By.cssSelector("[data-test='title']");
    private final By botaoAdicionarMochila = By.id("add-to-cart-sauce-labs-backpack");
    private final By contadorCarrinho      = By.cssSelector("[data-test='shopping-cart-badge']");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    // ---------------------- AÇÕES ----------------------

    /** Lê o título da página (esperado: "Products"). */
    public String obterTitulo() {
        return esperarVisivel(titulo).getText();
    }

    /**
     * Clica em "Add to cart" da mochila.
     * Retorna "this" para permitir encadear a próxima ação.
     */
    public ProductsPage adicionarMochilaAoCarrinho() {
        clicar(botaoAdicionarMochila);
        return this;
    }

    /** Lê o número que aparece no ícone do carrinho. */
    public String obterQuantidadeNoCarrinho() {
        return esperarVisivel(contadorCarrinho).getText();
    }
}
