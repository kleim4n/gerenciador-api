package br.com.kleiman.gerenciador.controller;

import java.util.List;

import br.com.kleiman.gerenciador.model.request.ClientePost;
import br.com.kleiman.gerenciador.model.response.ClienteResponse;
import br.com.kleiman.gerenciador.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> cria(@RequestBody ClientePost clientePost) {
        return ResponseEntity
                .ok()
                .body(clienteService.cria(clientePost));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> lista() {
        return ResponseEntity
                .ok()
                .body(clienteService.lista());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> busca(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(clienteService.busca(id));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponse> buscaCpf(@PathVariable String cpf) {
        return ResponseEntity
                .ok()
                .body(clienteService.buscaCpf(cpf));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleta(@PathVariable long id) {
        clienteService.deleta(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/{id}/nome/{nome}")
    public ResponseEntity<ClienteResponse> atualizaNome(@PathVariable long id, @PathVariable String nome) {
        return ResponseEntity
                .ok()
                .body(clienteService.atualizaNome(id, nome));
    }

}
