package br.com.kleiman.gerenciador.controller;

import java.util.List;

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
import br.com.kleiman.gerenciador.model.response.ProdutoResponse;
import br.com.kleiman.gerenciador.model.request.ProdutoPost;
import br.com.kleiman.gerenciador.service.ProdutoService;

@RestController
@RequestMapping("/api")
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

    @PostMapping("/v1/produtos")
    public ResponseEntity<ProdutoResponse> cria(@RequestBody ProdutoPost produtoPost) {
        return ResponseEntity
                .ok()
                .body(produtoService.cria(produtoPost));
    }

    @GetMapping("/v1/produtos")
    public ResponseEntity<List<ProdutoResponse>> lista() {
        return ResponseEntity
                .ok()
                .body(produtoService.lista());
    }

    @GetMapping("/v1/produtos/{id}")
    public ResponseEntity<ProdutoResponse> busca(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(produtoService.busca(id));
    }

    @DeleteMapping("/v1/produtos/{id}")
    public ResponseEntity<Void> deleta(@PathVariable long id) {
        produtoService.deleta(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/v1/produtos/{id}/estoque/{estoque}")
    public ResponseEntity<ProdutoResponse> atualizaEstoque(@PathVariable long id, @PathVariable int estoque) {
        return ResponseEntity
                .ok()
                .body(produtoService.atualizaEstoque(id, estoque));
    }

    @PatchMapping("/v1/produtos/{id}/preco/{preco}")
    public ResponseEntity<ProdutoResponse> atualizaPreco(@PathVariable long id, @PathVariable double preco) {
        return ResponseEntity
                .ok()
                .body(produtoService.atualizaPreco(id, preco));
    }
}
