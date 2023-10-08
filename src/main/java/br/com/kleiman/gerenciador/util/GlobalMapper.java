package br.com.kleiman.gerenciador.util;

import br.com.kleiman.gerenciador.model.entity.Cliente;
import br.com.kleiman.gerenciador.model.entity.ItemVenda;
import br.com.kleiman.gerenciador.model.entity.Venda;
import br.com.kleiman.gerenciador.model.request.ClientePost;
import br.com.kleiman.gerenciador.model.request.ItemVendaPost;
import br.com.kleiman.gerenciador.model.request.VendaPost;
import br.com.kleiman.gerenciador.model.response.ClienteResponse;
import br.com.kleiman.gerenciador.model.response.ItemVendaRespose;
import br.com.kleiman.gerenciador.model.response.ProdutoResponse;
import br.com.kleiman.gerenciador.model.entity.Produto;
import br.com.kleiman.gerenciador.model.request.ProdutoPost;
import br.com.kleiman.gerenciador.model.response.VendaResponse;

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

    public static Venda VendaMapper(VendaPost vendaPost) {
        return Venda.builder()
                .cliente_id(vendaPost.cliente_id())
                .desconto(vendaPost.desconto())
                .build();
    }

    public static VendaResponse VendaMapper(Venda venda) {
        return new VendaResponse(
                venda.getId(),
                venda.getCliente_id(),
                venda.getData(),
                venda.getRealizada(),
                venda.getDesconto()
        );
    }

    public static ItemVenda ItemVendaMapper(ItemVendaPost itemVendaPost) {
        return ItemVenda.builder()
                .produto_id(itemVendaPost.produtoId())
                .venda_id(itemVendaPost.vendaId())
                .quantidade(itemVendaPost.quantidade())
                .desconto(itemVendaPost.desconto())
                .build();
    }

    public static ItemVendaRespose ItemVendaMapper(ItemVenda itemVenda) {
        return new ItemVendaRespose(
                itemVenda.getId(),
                itemVenda.getProduto_id(),
                itemVenda.getVenda_id(),
                itemVenda.getQuantidade(),
                itemVenda.getPreco(),
                itemVenda.getDesconto()
        );
    }
}
