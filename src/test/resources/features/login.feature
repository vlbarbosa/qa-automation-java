# language: pt
# ============================================================================
# CENÁRIOS BDD EM GHERKIN
# ============================================================================
# BDD (Behavior Driven Development) descreve o COMPORTAMENTO do sistema em
# linguagem de negócio, que PO, desenvolvedores e QA entendem.
# A primeira linha "# language: pt" permite escrever em português.
#
# Palavras-chave:
#   Funcionalidade     -> o que está sendo testado
#   Cenário            -> um caso de teste
#   Dado               -> pré-condição (contexto)
#   Quando             -> ação do usuário
#   Então              -> resultado esperado
#   Esquema do Cenário -> mesmo cenário com vários dados (tabela Exemplos)
#
# Neste projeto o arquivo serve como DOCUMENTAÇÃO dos cenários automatizados
# em LoginWebTest.java. Para executá-lo, seria adicionado o Cucumber, que
# liga cada frase a um método Java (as "step definitions").
# ============================================================================

Funcionalidade: Login no Sauce Demo
  Como cliente da loja
  Quero acessar minha conta
  Para poder comprar produtos

  Cenário: Login com credenciais válidas
    Dado que estou na tela de login
    Quando informo o usuário "standard_user" e a senha "secret_sauce"
    Então devo ver a tela de produtos

  Esquema do Cenário: Login com dados inválidos
    Dado que estou na tela de login
    Quando informo o usuário "<usuario>" e a senha "<senha>"
    Então devo ver a mensagem de erro "<mensagem>"

    Exemplos:
      | usuario         | senha        | mensagem             |
      | standard_user   | senha_errada | do not match         |
      | locked_out_user | secret_sauce | locked out           |
      |                 | secret_sauce | Username is required |
      | standard_user   |              | Password is required |
