import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;

public class ProdutoTest {

    public static final String LOCALHOST = "http://localhost:8080";
    public static final String V1_PRODUTOS_ID = "/api/v1/produtos/{id}";

    record ProdutoPost(String descricao, Double preco, Integer estoque) {}
    static ProdutoPost produtoPost = new ProdutoPost("Produto 1", 10.0, 1);
    static ProdutoPost produto2Post = new ProdutoPost("Produto 2", 10.0, 1);
    @Test
    void testCrudProduto() {
        long id =
            given()
                .baseUri(LOCALHOST)
                    .contentType(JSON)
                .body(produtoPost)
                    .log().all()
            .when()
                .post("/api/v1/produtos")
            .then()
                .statusCode(SC_OK)
                .extract()
                .jsonPath()
                .getLong("id");
        given()
                .baseUri(LOCALHOST)
                .pathParam("id", id)
            .when()
                .get(V1_PRODUTOS_ID)
            .then()
                .statusCode(SC_OK)
                .equals(produto2Post);// não funciona
        given()
                .baseUri(LOCALHOST)
                .pathParam("id", id)
            .when()
                .delete(V1_PRODUTOS_ID)
            .then()
                .statusCode(SC_NO_CONTENT);
    }

}
