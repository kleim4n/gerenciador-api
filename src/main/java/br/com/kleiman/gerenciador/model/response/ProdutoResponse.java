package br.com.kleiman.gerenciador.model.response;

public record ProdutoResponse(
        Long id,
        String descricao,
        Double preco,
        Integer estoque) {
}
