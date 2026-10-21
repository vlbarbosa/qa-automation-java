package com.qa.automation.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * ============================================================================
 * BASE PAGE - classe "mãe" de todas as páginas
 * ============================================================================
 *
 * CONCEITOS DE ORIENTAÇÃO A OBJETOS APLICADOS AQUI:
 *
 *  - HERANÇA: LoginPage e ProductsPage "estendem" (extends) esta classe.
 *    Assim, herdam o driver, a espera e os métodos clicar/escrever, sem
 *    repetir código em cada página.
 *
 *  - ABSTRAÇÃO: a classe é "abstract", ou seja, não dá para criar um objeto
 *    BasePage diretamente. Ela existe só para ser herdada.
 *
 *  - ENCAPSULAMENTO: os atributos são "protected" (só as classes filhas
 *    enxergam), e o teste nunca mexe no driver por aqui.
 *
 * ESPERAS EXPLÍCITAS (pergunta clássica de entrevista):
 *  Páginas web carregam de forma assíncrona. Se tentarmos clicar num botão
 *  que ainda não apareceu, o teste falha. A solução correta é a ESPERA
 *  EXPLÍCITA (WebDriverWait): esperar uma CONDIÇÃO (ficar visível, ficar
 *  clicável) por até X segundos. Assim que a condição é atendida, segue.
 *  NUNCA usar Thread.sleep(): ele espera um tempo fixo, deixando o teste
 *  lento quando a página é rápida e instável quando ela é lenta.
 * ============================================================================
 */
public abstract class BasePage {

    // O driver é o "controle remoto" do navegador.
    // "final" = depois de definido no construtor, não muda mais.
    protected final WebDriver driver;

    // Objeto de espera explícita, configurado com tempo máximo de 10 segundos.
    protected final WebDriverWait wait;

    /**
     * Construtor: executado quando uma página é criada (new LoginPage(driver)).
     * Recebe o driver do teste e prepara a espera.
     */
    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Espera o elemento ficar VISÍVEL na tela e o devolve.
     *
     * @param locator a forma de encontrar o elemento (By.id, By.cssSelector...)
     * @return o elemento já visível
     */
    protected WebElement esperarVisivel(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Espera o elemento ficar CLICÁVEL (visível e habilitado) e clica nele.
     */
    protected void clicar(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Espera o campo aparecer, apaga o conteúdo anterior e digita o texto.
     * O clear() evita que o texto novo seja somado a algo já preenchido.
     */
    protected void escrever(By locator, String texto) {
        WebElement campo = esperarVisivel(locator);
        campo.clear();
        campo.sendKeys(texto);
    }
}
