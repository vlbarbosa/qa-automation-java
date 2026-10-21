# Instalação e execução

Guia para rodar o projeto no **Visual Studio Code** (Windows). Não é preciso instalar o Maven: a extensão de Java do VS Code já baixa as bibliotecas e roda os testes.

## 1. Programas necessários

| Programa | Onde baixar | Observação |
| --- | --- | --- |
| Java JDK 25 LTS | [adoptium.net](https://adoptium.net) (Temurin 25) | Na instalação, marcar **Set JAVA_HOME variable** |
| Visual Studio Code | [code.visualstudio.com](https://code.visualstudio.com) | O azul, não o "Visual Studio" roxo |
| Git | [git-scm.com](https://git-scm.com) | Instalação padrão |
| Google Chrome | [google.com/chrome](https://www.google.com/chrome) | Necessário para os testes Web |

Depois de instalar, reinicie o computador.

## 2. Extensão do VS Code

1. Abra o VS Code e aperte `Ctrl+Shift+X`.
2. Instale **Extension Pack for Java** (Microsoft).

## 3. Abrir o projeto

1. **File → Open Folder** e escolha a pasta `qa-automation-java`.
2. Confirme "Yes, I trust the authors".
3. Aguarde a importação no rodapé (2 a 5 minutos na primeira vez) até aparecer **Java: Ready**.

## 4. Rodar os testes

1. Clique no ícone **Testing** (béquer) na barra lateral.
2. Rode **PostsApiTest** (API) e **LoginWebTest** (Web) clicando no ▶.
3. Não rode o **LoginMobileTest** nem "Run All" sem o ambiente Appium: ele vai falhar.

## 5. Publicar no GitHub

1. Terminal (**Terminal → New Terminal**), apenas na primeira vez:
   ```bash
   git config --global user.name "Seu Nome"
   git config --global user.email "seu-email@exemplo.com"
   ```
2. Ícone **Source Control** → **Publish to GitHub** → entre na conta pelo navegador.
3. Escolha **public repository**.
4. Abra o repositório e confira a aba **Actions**.

## 6. (Opcional) Ambiente mobile

1. [Node.js LTS](https://nodejs.org)
2. [Android Studio](https://developer.android.com/studio) → criar um emulador no Device Manager
3. No terminal:
   ```bash
   npm i -g appium
   appium driver install uiautomator2
   appium
   ```
4. [Appium Inspector](https://github.com/appium/appium-inspector/releases) para mapear os elementos
5. Baixe o .apk do app de exemplo (veja `apps/LEIA-ME.md`) e rode o `LoginMobileTest`.

## Problemas comuns

| Problema | Solução |
| --- | --- |
| Java não encontrado | Reinicie o PC; confira se o JDK 25 foi instalado com JAVA_HOME |
| Nenhum teste aparece no béquer | Espere o "Java: Importing" terminar; se travar, `Ctrl+Shift+P` → **Java: Clean Java Language Server Workspace** |
| Erro de driver no teste Web | Atualize o Chrome |
| Linhas vermelhas no código | Somem quando a importação termina |
