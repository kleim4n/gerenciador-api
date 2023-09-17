package br.com.kleiman.gerenciador.service;

import br.com.kleiman.gerenciador.model.entity.Produto;
import br.com.kleiman.gerenciador.util.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.kleiman.gerenciador.model.response.ProdutoResponse;
import br.com.kleiman.gerenciador.model.request.ProdutoPost;
import br.com.kleiman.gerenciador.repository.ProdutoRepository;
import br.com.kleiman.gerenciador.util.GlobalMapper;

import java.util.List;

import static br.com.kleiman.gerenciador.util.GlobalMapper.ProdutoMapper;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository produtoRepository;

    public List<ProdutoResponse> lista() {
        return produtoRepository
                .findAll()
                .stream()
                .map(GlobalMapper::ProdutoMapper)
                .toList();
    }

    public ProdutoResponse busca(long id) {
        return produtoRepository
                .findById(id)
                .map(GlobalMapper::ProdutoMapper)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Produto não encontrado"));
    }

    public ProdutoResponse cria(ProdutoPost produtoPost) {
        if(produtoPost.descricao() == null)
            throw new GlobalExceptionHandler.BadRequestException("Descrição de produto é obrigatória");
        if(produtoPost.descricao().isBlank())
            throw new GlobalExceptionHandler.UnprocessableException("Descrição não pode ser vazia");
        if(produtoPost.preco() < 0)
            throw new GlobalExceptionHandler.UnprocessableException("Preço não pode ser negativo");
        if(produtoPost.estoque() < 0)
            throw new GlobalExceptionHandler.UnprocessableException("Estoque não pode ser negativo");
        if(produtoRepository.existsByDescricao(produtoPost.descricao()))
            throw new GlobalExceptionHandler.UnprocessableException("Descrição já cadastrada");
        return ProdutoMapper(
                produtoRepository.save(
                        ProdutoMapper(produtoPost)));
    }

    public void deleta(long id) {
        produtoRepository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Produto não encontrado"));
        produtoRepository.deleteById(id);
    }

    public ProdutoResponse atualizaEstoque(long id, int estoque) {
        if(estoque < 0)
            throw new GlobalExceptionHandler.UnprocessableException("Estoque não pode ser negativo");
        Produto produto = produtoRepository.findById(id).orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Produto não encontrado"));
        return ProdutoMapper(produtoRepository.save(
                Produto.builder()
                        .id(produto.getId())
                        .descricao(produto.getDescricao())
                        .preco(produto.getPreco())
                        .estoque(estoque)
                        .build()
        ));
    }

    public ProdutoResponse atualizaPreco(long id, double preco) {
        if(preco < 0)
            throw new GlobalExceptionHandler.UnprocessableException("Preço não pode ser negativo");
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Produto não encontrado"));
        return ProdutoMapper(produtoRepository.save(
                Produto.builder()
                        .id(produto.getId())
                        .descricao(produto.getDescricao())
                        .preco(preco)
                        .estoque(produto.getEstoque())
                        .build()
        ));
    }

}
