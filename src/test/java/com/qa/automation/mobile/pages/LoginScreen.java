package com.qa.automation.mobile.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

/**
 * ============================================================================
 * LOGIN SCREEN - Page Object MOBILE (serve para Android E iOS)
 * ============================================================================
 *
 * No mobile, o Page Object segue a mesma ideia da Web, mas aqui usamos o
 * PAGE FACTORY do Appium, que permite declarar os locators com anotações:
 *
 *   @AndroidFindBy   -> locator usado quando o teste roda no Android
 *   @iOSXCUITFindBy  -> locator usado quando o teste roda no iOS
 *
 * O AppiumFieldDecorator verifica em qual plataforma o teste está rodando e
 * escolhe o locator certo automaticamente. Resultado: UMA classe, DUAS
 * plataformas, sem duplicar código. (Ótimo ponto para citar na entrevista.)
 *
 * LOCATORS NO MOBILE - ordem de preferência:
 *   1. accessibility id  (o melhor: estável e igual nas duas plataformas)
 *   2. id / resource-id  (Android)
 *   3. UiSelector (Android) / iOS predicate ou class chain (iOS)
 *   4. xpath             (último recurso: lento no mobile)
 *  Para descobrir os locators, usa-se o APPIUM INSPECTOR.
 *
 * APP DE EXEMPLO: "My Demo App" da Sauce Labs
 *  (github.com/saucelabs/my-demo-app-android).
 *  Atenção: confira os ids no Appium Inspector, pois podem mudar entre
 *  versões do app.
 * ============================================================================
 */
public class LoginScreen {

    // ---------------------- LOCATORS ----------------------
    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameET")
    @iOSXCUITFindBy(accessibility = "Username input field")
    private WebElement campoUsuario;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/passwordET")
    @iOSXCUITFindBy(accessibility = "Password input field")
    private WebElement campoSenha;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/loginBtn")
    @iOSXCUITFindBy(accessibility = "Login button")
    private WebElement botaoLogin;

    /**
     * Construtor: "liga" os campos acima aos elementos reais do app.
     * O Duration de 10 segundos funciona como espera: o Appium tenta
     * encontrar o elemento por até 10 s antes de falhar.
     *
     * Recebe AppiumDriver (classe mãe de AndroidDriver e IOSDriver).
     * Isso é POLIMORFISMO: o mesmo método aceita drivers diferentes.
     */
    public LoginScreen(AppiumDriver driver) {
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    // ---------------------- AÇÕES ----------------------

    /** Preenche usuário e senha e toca no botão de login. */
    public void login(String usuario, String senha) {
        campoUsuario.sendKeys(usuario);
        campoSenha.sendKeys(senha);
        botaoLogin.click();
    }
}
