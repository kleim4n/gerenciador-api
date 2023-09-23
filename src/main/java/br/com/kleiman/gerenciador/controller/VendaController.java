package br.com.kleiman.gerenciador.controller;

import java.util.List;

import br.com.kleiman.gerenciador.model.request.VendaPost;
import br.com.kleiman.gerenciador.model.response.VendaResponse;
import br.com.kleiman.gerenciador.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/vendas")
public class VendaController {
    @Autowired
    private VendaService vendaService;

    @PostMapping
    public ResponseEntity<VendaResponse> cria(@RequestBody VendaPost vendaPost) {
        return ResponseEntity
                .ok()
                .body(vendaService.cria(vendaPost));
    }

    @PostMapping("/{id}/finaliza")
    public ResponseEntity<VendaResponse> finaliza(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(vendaService.finaliza(id));
    }

    @PostMapping("/{id}/cancela")
    public ResponseEntity<VendaResponse> cancela(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(vendaService.cancela(id));
    }

    @GetMapping
    public ResponseEntity<List<VendaResponse>> lista() {
        return ResponseEntity
                .ok()
                .body(vendaService.lista());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponse> busca(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(vendaService.busca(id));
    }

    @GetMapping("/cliente/cpf/{cpf}")
    public ResponseEntity<List<VendaResponse>> buscaCliente(@PathVariable String cpf) {
        return ResponseEntity
                .ok()
                .body(vendaService.buscaCliente(cpf));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleta(@PathVariable long id) {
        vendaService.deleta(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/{id}/desconto/{desconto}")
    public ResponseEntity<VendaResponse> atualizaDesconto(@PathVariable long id, @PathVariable double desconto) {
        return ResponseEntity
                .ok()
                .body(vendaService.atualizaDesconto(id, desconto));
    }
}
