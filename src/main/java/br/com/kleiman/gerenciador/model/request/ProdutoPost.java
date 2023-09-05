package br.com.kleiman.gerenciador.model.request;

public record ProdutoPost(
        String descricao,
        Double preco,
        Integer estoque) {
}
