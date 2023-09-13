import org.junit.jupiter.api.Test;

import java.util.Date;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.is;

public class ProdutoTest {

    public static final String LOCALHOST = "http://localhost:8080";
    public static final String V1_PRODUTOS_ID = "/api/v1/produtos/{id}";

    record ProdutoPost(String descricao, Double preco, Integer estoque) {}
    static ProdutoPost produtoPost = new ProdutoPost("Produto %s".formatted(new Date().getTime()), 10.0, 1);
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
                .body("descricao", is(produtoPost.descricao()))
                .body("preco", is(produtoPost.preco().floatValue()))
                .body("estoque", is(produtoPost.estoque()));
        given()
                .baseUri(LOCALHOST)
                .pathParam("id", id)
                .pathParam("estoque", 2)
            .when()
                .patch("/api/v1/produtos/{id}/estoque/{estoque}")
            .then()
                .statusCode(SC_OK)
                .log().all()
                .body("descricao", is(produtoPost.descricao()))
                .body("preco", is(produtoPost.preco().floatValue()))
                .body("estoque", is(2));

        given()
                .baseUri(LOCALHOST)
                .pathParam("id", id)
            .when()
                .delete(V1_PRODUTOS_ID)
            .then()
                .statusCode(SC_NO_CONTENT);
    }

}
