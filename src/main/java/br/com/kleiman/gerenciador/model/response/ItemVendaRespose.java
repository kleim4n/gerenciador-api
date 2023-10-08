package br.com.kleiman.gerenciador.model.response;

public record ItemVendaRespose(
        Long id,
        Long produtoId,
        Long vendaId,
        Integer quantidade,
        Double preco,
        Double desconto
) {
}
