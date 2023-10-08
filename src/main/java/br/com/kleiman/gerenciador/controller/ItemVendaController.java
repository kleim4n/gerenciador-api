package br.com.kleiman.gerenciador.controller;

import java.util.List;

import br.com.kleiman.gerenciador.model.request.ItemVendaPost;
import br.com.kleiman.gerenciador.model.response.ItemVendaRespose;
import br.com.kleiman.gerenciador.service.ItemVendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/item-vendas")
public class ItemVendaController {
    @Autowired
    private ItemVendaService itemVendaService;

    @PostMapping
    public ResponseEntity<ItemVendaRespose> cria(@RequestBody ItemVendaPost itemVendaPost) {
        return ResponseEntity.ok(itemVendaService.cria(itemVendaPost));
    }

    @GetMapping
    public ResponseEntity<List<ItemVendaRespose>> lista() {
        return ResponseEntity.ok(itemVendaService.lista());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemVendaRespose> busca(@PathVariable long id) {
        return ResponseEntity.ok(itemVendaService.busca(id));
    }

    @GetMapping("/venda/{id}")
    public ResponseEntity<List<ItemVendaRespose>> listaPorVenda(@PathVariable long id) {
        return ResponseEntity.ok(itemVendaService.listaPorVenda(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleta(@PathVariable long id) {
        itemVendaService.deleta(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/venda/{id}")
    public ResponseEntity<Void> deletaPorVenda(@PathVariable long id) {
        itemVendaService.deletaPorVenda(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/quantidade/{quantidade}")
    public ResponseEntity<ItemVendaRespose> atualizaQuantidade(@PathVariable long id, @PathVariable int quantidade) {
        return ResponseEntity.ok(itemVendaService.atualizaQuantidade(id, quantidade));
    }

    @PatchMapping("/{id}/desconto/{desconto}")
    public ResponseEntity<ItemVendaRespose> atualizaDesconto(@PathVariable long id, @PathVariable double desconto) {
        return ResponseEntity.ok(itemVendaService.atualizaDesconto(id, desconto));
    }
}
