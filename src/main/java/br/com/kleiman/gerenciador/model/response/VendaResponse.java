package br.com.kleiman.gerenciador.model.response;

import java.time.LocalDateTime;

public record VendaResponse(
        Long id,
        Long cliente_id,
        LocalDateTime data,
        Boolean realizada,
        Double desconto) {
}
