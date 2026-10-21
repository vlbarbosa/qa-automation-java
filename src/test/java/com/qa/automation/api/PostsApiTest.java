package com.qa.automation.api;

// ---------------------------------------------------------------------------
// IMPORTS: trazem as classes das bibliotecas que vamos usar.
// ---------------------------------------------------------------------------
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// "import static" permite chamar os métodos direto, sem o nome da classe.
// Ex.: escrevemos given() em vez de RestAssured.given().
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
// Hamcrest: biblioteca de "matchers" (comparadores) usados nas validações,
// como equalTo(), notNullValue(), hasSize(), everyItem().
import static org.hamcrest.Matchers.*;

/**
 * ============================================================================
 * TESTES DE API REST - recurso "posts"
 * ============================================================================
 *
 * API testada: JSONPlaceholder (https://jsonplaceholder.typicode.com)
 * É uma API pública e gratuita feita para estudo e testes.
 * Observação: ela SIMULA criação/alteração/exclusão (responde como se tivesse
 * salvo, mas não grava de verdade). Isso é suficiente para praticar.
 *
 * POR QUE TESTAR API?
 *  - É mais rápido e estável que testar pela tela (UI).
 *  - Encontra o erro na origem (no back-end).
 *  - Pela "pirâmide de testes", regras de negócio devem ser validadas
 *    preferencialmente na API, deixando a UI para os fluxos críticos.
 *
 * PADRÃO given / when / then (vem do BDD):
 *  - given()  -> DADO: o que preparo para a requisição (headers, body, params)
 *  - when()   -> QUANDO: a ação, ou seja, a chamada HTTP (get, post, put...)
 *  - then()   -> ENTÃO: as validações da resposta (status, corpo, tempo)
 *
 * O QUE ESTA CLASSE COBRE:
 *  - GET (consulta), POST (criação), PUT (atualização), DELETE (exclusão)
 *  - Validação de contrato com JSON Schema
 *  - Query params (filtros na URL)
 *  - Cenários negativos (404)
 *  - Encadeamento de chamadas (usar o resultado de uma na outra)
 *
 * @Tag("api") permite rodar só estes testes: mvn test -Dgroups=api
 * ============================================================================
 */
@Tag("api")
@DisplayName("API de Posts")
class PostsApiTest {

    /**
     * @BeforeAll: roda UMA VEZ antes de todos os testes da classe.
     * Usamos para configurações comuns. O método precisa ser "static".
     */
    @BeforeAll
    static void configurar() {
        // URL base: todas as requisições começam com este endereço.
        // Assim, nos testes escrevemos só o caminho, ex.: "/posts/1".
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        // Se um teste falhar, imprime no console a requisição e a resposta
        // completas. Ajuda MUITO a investigar a causa da falha.
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // ------------------------------------------------------------------------
    // GET - CONSULTAR UM REGISTRO
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("GET /posts/1 retorna 200 e os campos esperados")
    void deveBuscarPostPorId() {
        given()
            // Nada a preparar: requisição simples.
        .when()
            .get("/posts/1")                        // GET https://.../posts/1
        .then()
            .statusCode(200)                         // 200 = OK (sucesso)
            .contentType(ContentType.JSON)           // resposta deve ser JSON
            .body("id", equalTo(1))                  // campo "id" deve ser 1
            .body("userId", notNullValue())          // "userId" deve existir
            .body("title", not(emptyOrNullString())) // "title" não pode ser vazio
            .time(lessThan(5000L));                  // responder em menos de 5 s
    }

    // ------------------------------------------------------------------------
    // TESTE DE CONTRATO
    // ------------------------------------------------------------------------
    /**
     * Teste de contrato verifica o FORMATO da resposta: se os campos
     * obrigatórios existem e se os tipos estão corretos (número, texto...).
     * O contrato fica no arquivo src/test/resources/schemas/post-schema.json.
     * Se o back-end mudar o formato e quebrar quem consome a API,
     * este teste falha e avisa.
     */
    @Test
    @DisplayName("GET /posts/1 respeita o contrato (JSON Schema)")
    void deveRespeitarContrato() {
        given()
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

    // ------------------------------------------------------------------------
    // GET - LISTAR TODOS
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("GET /posts retorna lista com 100 posts")
    void deveListarPosts() {
        given()
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            // "$" representa a raiz do JSON. Aqui a raiz é uma lista,
            // e validamos que ela tem exatamente 100 itens.
            .body("$", hasSize(100));
    }

    // ------------------------------------------------------------------------
    // GET COM QUERY PARAM (filtro)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("GET com query param filtra posts por usuário")
    void deveFiltrarPorUsuario() {
        given()
            // queryParam adiciona "?userId=2" no fim da URL.
            .queryParam("userId", 2)
        .when()
            .get("/posts")                           // GET /posts?userId=2
        .then()
            .statusCode(200)
            // everyItem: TODOS os itens da lista devem ter userId igual a 2.
            // Assim garantimos que o filtro realmente funciona.
            .body("userId", everyItem(equalTo(2)));
    }

    // ------------------------------------------------------------------------
    // POST - CRIAR
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("POST /posts cria um post e retorna 201")
    void deveCriarPost() {
        // Corpo (body) da requisição em formato JSON.
        // As três aspas (""") são um "text block" do Java 15+, que permite
        // escrever textos de várias linhas sem precisar escapar as aspas.
        String corpo = """
                {
                  "title": "Teste automatizado",
                  "body": "Criado pelo RestAssured",
                  "userId": 1
                }
                """;

        given()
            .contentType(ContentType.JSON)           // avisa que enviamos JSON
            .body(corpo)                             // dados enviados
        .when()
            .post("/posts")
        .then()
            .statusCode(201)                         // 201 = Created (criado)
            .body("id", notNullValue())              // a API gerou um id
            .body("title", equalTo("Teste automatizado")); // devolveu o que enviamos
    }

    // ------------------------------------------------------------------------
    // PUT - ATUALIZAR (substitui o registro inteiro)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("PUT /posts/1 atualiza o post")
    void deveAtualizarPost() {
        given()
            .contentType(ContentType.JSON)
            // Aqui o JSON está numa linha só; as aspas internas são escapadas com \"
            .body("{\"id\": 1, \"title\": \"Atualizado\", \"body\": \"Novo texto\", \"userId\": 1}")
        .when()
            .put("/posts/1")
        .then()
            .statusCode(200)
            .body("title", equalTo("Atualizado"));
    }

    // ------------------------------------------------------------------------
    // DELETE - EXCLUIR
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("DELETE /posts/1 retorna 200")
    void deveExcluirPost() {
        given()
        .when()
            .delete("/posts/1")
        .then()
            // Muitas APIs retornam 204 (No Content) no DELETE.
            // Esta retorna 200. Sempre confira o que a documentação da API diz.
            .statusCode(200);
    }

    // ------------------------------------------------------------------------
    // CENÁRIO NEGATIVO + TESTE PARAMETRIZADO
    // ------------------------------------------------------------------------
    /**
     * Cenário negativo: testar o que acontece quando algo dá errado.
     * Um bom QA testa tanto o "caminho feliz" quanto os erros.
     *
     * @ParameterizedTest roda o MESMO teste várias vezes com dados diferentes.
     * @ValueSource fornece os valores: o teste roda uma vez com 0 e outra com 9999.
     * Isso evita copiar e colar o mesmo teste.
     */
    @ParameterizedTest(name = "GET /posts/{0} retorna 404 (cenário negativo)")
    @ValueSource(ints = {0, 9999})
    void deveRetornar404ParaPostInexistente(int id) {
        given()
        .when()
            .get("/posts/" + id)
        .then()
            .statusCode(404);                        // 404 = Not Found
    }

    // ------------------------------------------------------------------------
    // ENCADEAMENTO DE CHAMADAS
    // ------------------------------------------------------------------------
    /**
     * Fluxo real: pega um dado da resposta de uma chamada e usa na próxima.
     * Ex. no dia a dia: fazer login, extrair o token e usá-lo nas outras
     * requisições; ou criar um cliente e depois consultar pelo id gerado.
     */
    @Test
    @DisplayName("Encadeamento: busca um post e depois os comentários dele")
    void deveEncadearChamadas() {
        // 1ª chamada: extract().path("id") tira o valor do campo "id" da resposta.
        int postId = given()
            .when()
                .get("/posts/1")
            .then()
                .statusCode(200)
                .extract().path("id");

        // 2ª chamada: usa o id extraído como filtro.
        given()
            .queryParam("postId", postId)
        .when()
            .get("/comments")
        .then()
            .statusCode(200)
            // Todos os comentários devem pertencer ao post consultado...
            .body("postId", everyItem(equalTo(postId)))
            // ...e todo e-mail deve conter "@" (validação simples de formato).
            .body("email", everyItem(containsString("@")));
    }
}
