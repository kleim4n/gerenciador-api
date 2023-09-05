package br.com.kleiman.gerenciador.service;

import br.com.kleiman.gerenciador.model.response.ProdutoResponse;
import br.com.kleiman.gerenciador.model.entity.Produto;
import br.com.kleiman.gerenciador.model.request.ProdutoPost;
import br.com.kleiman.gerenciador.repository.ProdutoRepository;
import br.com.kleiman.gerenciador.util.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static br.com.kleiman.gerenciador.util.GlobalMapper.ProdutoMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class ProdutoServiceTest {
    @Mock
    private ProdutoRepository produtoRepository;
    @InjectMocks
    private ProdutoService produtoService;
    private static Produto produto1 = new Produto(1L, "Produto 1", 10.0, 10);
    private static Produto produto2 = new Produto(2L, "Produto 2", 20.0, 20);
    private static ProdutoPost produto1Post = new ProdutoPost("Produto 1", 10.0, 10);

    @Test
    @DisplayName("Lista todos os produtos com sucesso")
    void lista() {
        when(produtoRepository.findAll()).thenReturn(List.of(
                produto1,
                produto2
        ));
        List<ProdutoResponse> produtos = produtoService.lista();
        assertEquals(2, produtos.size());
        assertEquals(ProdutoMapper(produto1), produtos.get(0));
        assertEquals(ProdutoMapper(produto2), produtos.get(1));
    }
    @Test
    @DisplayName("Busca produto por id com sucesso")
    void busca() {
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(produto1));
        assertEquals(ProdutoMapper(produto1), produtoService.busca(1L));
    }
    @Test
    @DisplayName("Busca produto por id inexistente")
    void buscaNotFound() {
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> produtoService.busca(1L));
    }
    @Test
    @DisplayName("Cria produto com sucesso")
    void cria() {
        when(produtoRepository.save(any())).thenReturn(produto1);
        ProdutoResponse produtoResponse = produtoService.cria(produto1Post);
        assertEquals(produto1.getId(), produtoResponse.id());
        assertEquals(produto1.getDescricao(), produtoResponse.descricao());
        assertEquals(produto1.getPreco(), produtoResponse.preco());
        assertEquals(produto1.getEstoque(), produtoResponse.estoque());
    }
    static Stream<Arguments> ProdutoUnprocessableExceptionProvider() {
        return Stream.of(
                Arguments.of(Named.of("Descrição String vazia", new ProdutoPost("", 10.0, 10)), new GlobalExceptionHandler.UnprocessableException("")),
                Arguments.of(Named.of("Descrição String com espaços", new ProdutoPost(" ", 10.0, 10)), new GlobalExceptionHandler.UnprocessableException("")),
                Arguments.of(Named.of("Preço negativo", new ProdutoPost("Produto 1", -10.0, 10)), new GlobalExceptionHandler.UnprocessableException("")),
                Arguments.of(Named.of("Estoque negativo", new ProdutoPost("Produto 1", 10.0, -10)), new GlobalExceptionHandler.UnprocessableException(""))
        );
    }
    @ParameterizedTest(name = "{0}")
    @DisplayName("Falha ao criar produto")
    @MethodSource("ProdutoUnprocessableExceptionProvider")
    void testCriaExceptions(ProdutoPost produtoPost, RuntimeException expectedException) {
        assertThrows(expectedException.getClass(), () -> produtoService.cria(produtoPost));
    }
    @Test
    @DisplayName("Falha ao criar produto com descrição repetida")
    void criaDescricaoRepetida() {
        when(produtoRepository.existsByDescricao("Produto 1")).thenReturn(Boolean.TRUE);
        assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> produtoService.cria(produto1Post));
    }
    @Test
    @DisplayName("Falha ao atualizar estoque com valor negativo")
    void testAtualizaEstoqueNegativo() {
        assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> produtoService.atualizaEstoque(1L, -1));
    }
    @Test
    @DisplayName("Falha ao atualizar preço com valor negativo")
    void atualizaPrecoNegativo() {
        assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> produtoService.atualizaPreco(1L, -1.0));
    }
    @Test
    @DisplayName("Falha ao atualizar estoque de produto inexistente")
    void atualizaEstoqueNotFound() {
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> produtoService.atualizaEstoque(1L, 20));
    }
    @Test
    @DisplayName("Falha ao atualizar preço de produto inexistente")
    void atualizaPrecoNotFound() {
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> produtoService.atualizaPreco(1L, 20.0));
    }
    @Test
    @DisplayName("Falha ao deletar produto inexistente")
    void deletaNotFound() {
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> produtoService.deleta(1L));
    }
}
