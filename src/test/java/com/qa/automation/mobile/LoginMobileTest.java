package com.qa.automation.mobile;

import com.qa.automation.mobile.pages.LoginScreen;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * ============================================================================
 * TESTE MOBILE - Login no app Android com Appium 2
 * ============================================================================
 *
 * ARQUITETURA DO APPIUM (pergunta frequente):
 *
 *   Teste Java  --HTTP-->  Servidor Appium  -->  Driver  -->  Celular/Emulador
 *   (cliente)              (Node.js, porta 4723)  (UiAutomator2 no Android,
 *                                                  XCUITest no iOS)
 *
 *  O teste não fala direto com o celular: ele envia comandos ao servidor
 *  Appium, que repassa ao driver da plataforma. É o mesmo protocolo
 *  (W3C WebDriver) do Selenium, por isso o código é parecido.
 *
 * NOVIDADES DO APPIUM 2:
 *  - Os drivers são instalados separadamente:
 *        appium driver install uiautomator2
 *  - Capabilities fora do padrão W3C usam o prefixo "appium:"
 *    (as classes Options, como UiAutomator2Options, fazem isso sozinhas).
 *
 * ESTE TESTE NÃO RODA POR PADRÃO. Pré-requisitos:
 *   1. Node.js instalado e: npm i -g appium
 *   2. appium driver install uiautomator2
 *   3. Android Studio com um emulador aberto
 *   4. Servidor ligado: abrir um terminal e digitar "appium"
 *   5. O arquivo .apk do app de exemplo
 *
 * Execução:
 *   mvn test -Dgroups=mobile -Dapp=C:/caminho/para/app.apk
 * ============================================================================
 */
@Tag("mobile")
@DisplayName("Login Mobile - Android")
class LoginMobileTest {

    // AndroidDriver: driver específico do Android (para iOS seria IOSDriver).
    private AndroidDriver driver;

    @BeforeEach
    void iniciarSessao() throws MalformedURLException {
        /*
         * CAPABILITIES: configurações enviadas ao abrir a sessão, dizendo
         * ao Appium ONDE e O QUE testar.
         */
        UiAutomator2Options opcoes = new UiAutomator2Options()
                // Sistema operacional do dispositivo
                .setPlatformName("Android")
                // Driver de automação (o padrão para Android)
                .setAutomationName("UiAutomator2")
                // Nome do dispositivo. "emulator-5554" é o nome padrão do
                // primeiro emulador. Pode ser trocado com -Ddevice=...
                .setDeviceName(System.getProperty("device", "emulator-5554"))
                // Caminho do .apk que será instalado e aberto
                .setApp(System.getProperty("app", "apps/mda-android.apk"))
                // Encerra a sessão se o Appium ficar 60 s sem receber comandos
                .setNewCommandTimeout(Duration.ofSeconds(60));

        // Conecta ao servidor Appium local (porta padrão 4723) e abre o app.
        driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), opcoes);
    }

    @AfterEach
    void encerrarSessao() {
        if (driver != null) {
            driver.quit(); // fecha a sessão e libera o dispositivo
        }
    }

    @Test
    @DisplayName("Login com usuário válido")
    void deveLogarNoApp() {
        // Usuário e senha de demonstração do próprio app de exemplo
        new LoginScreen(driver).login("bod@example.com", "10203040");

        // Validação simples: a sessão continua ativa após o login.
        // Num projeto real, validaríamos um elemento da tela seguinte
        // (ex.: título "Products" visível).
        assertNotNull(driver.getSessionId());
    }
}
