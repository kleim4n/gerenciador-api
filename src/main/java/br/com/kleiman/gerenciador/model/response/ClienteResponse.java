package br.com.kleiman.gerenciador.model.response;

public record ClienteResponse (
        Long id,
        String cpf,
        String nome) {
}
