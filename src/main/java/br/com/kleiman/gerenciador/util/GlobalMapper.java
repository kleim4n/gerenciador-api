package br.com.kleiman.gerenciador.util;

import br.com.kleiman.gerenciador.model.entity.Cliente;
import br.com.kleiman.gerenciador.model.request.ClientePost;
import br.com.kleiman.gerenciador.model.response.ClienteResponse;
import br.com.kleiman.gerenciador.model.response.ProdutoResponse;
import br.com.kleiman.gerenciador.model.entity.Produto;
import br.com.kleiman.gerenciador.model.request.ProdutoPost;

public class GlobalMapper {
    public static ProdutoResponse ProdutoMapper(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getEstoque());
    }

    public static Produto ProdutoMapper(ProdutoResponse produtoResponse) {
        return Produto.builder()
                .id(produtoResponse.id())
                .descricao(produtoResponse.descricao())
                .preco(produtoResponse.preco())
                .estoque(produtoResponse.estoque())
                .build();
    }

    public static Produto ProdutoMapper(ProdutoPost produtoPost) {
        return Produto.builder()
                .descricao(produtoPost.descricao())
                .preco(produtoPost.preco())
                .estoque(produtoPost.estoque())
                .build();
    }

    public static ClienteResponse ClienteMapper(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getCpf(),
                cliente.getNome());
    }

    public static Cliente ClienteMapper(ClientePost clientePost) {
        return Cliente.builder()
                .cpf(clientePost.cpf())
                .nome(clientePost.nome())
                .build();
    }
}
