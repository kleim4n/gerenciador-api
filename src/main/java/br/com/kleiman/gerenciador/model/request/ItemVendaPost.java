package br.com.kleiman.gerenciador.model.request;

public record ItemVendaPost(
        Long produtoId,
        Long vendaId,
        Integer quantidade,
        Double desconto
) {
}
