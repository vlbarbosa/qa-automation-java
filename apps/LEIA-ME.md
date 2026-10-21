# Pasta apps

Coloque aqui o arquivo `.apk` do aplicativo usado no teste mobile.

App de exemplo: **My Demo App** da Sauce Labs, disponível na página de releases do repositório
[saucelabs/my-demo-app-android](https://github.com/saucelabs/my-demo-app-android/releases).

Salve como `apps/mda-android.apk` ou informe outro caminho ao rodar:

```bash
mvn test -Dgroups=mobile -Dapp=C:/caminho/para/app.apk
```

Os arquivos `.apk` e `.ipa` estão no `.gitignore` para não subirem ao GitHub (são grandes).
