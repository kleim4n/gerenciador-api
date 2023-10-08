package br.com.kleiman.gerenciador.service;

import br.com.kleiman.gerenciador.model.entity.ItemVenda;
import br.com.kleiman.gerenciador.model.entity.Produto;
import br.com.kleiman.gerenciador.model.entity.Venda;
import br.com.kleiman.gerenciador.model.request.ItemVendaPost;
import br.com.kleiman.gerenciador.repository.ItemVendaRepository;
import br.com.kleiman.gerenciador.repository.ProdutoRepository;
import br.com.kleiman.gerenciador.repository.VendaRepository;
import br.com.kleiman.gerenciador.util.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static br.com.kleiman.gerenciador.util.GlobalMapper.ItemVendaMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemVendaServiceTest {
    @Mock
    private VendaRepository vendaRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private ItemVendaRepository itemVendaRepository;
    @InjectMocks
    private ItemVendaService itemVendaService;

    Venda vendaEmAberto = new Venda(1L, 1L, LocalDateTime.now(), false, 0.0);
    Venda vendaRealizada = new Venda(1L, 1L, LocalDateTime.now(), true, 0.0);
    Produto produto = new Produto(1L, "Produto A", 10.0, 10);
    ItemVenda itemVenda = new ItemVenda(1L, 1L, 1L, 1, 0.0, 1.0);

    @Test
    void testCria() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(produto));
        when(itemVendaRepository.save(any(ItemVenda.class))).thenReturn(itemVenda);
        assertEquals(ItemVendaMapper(itemVenda), itemVendaService.cria(new ItemVendaPost(1L, 1L, 1, 0.0)));
    }
    @Test
    void testCriaVendaNaoEncontrada() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        GlobalExceptionHandler.NotFoundException exception = assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> itemVendaService.cria(new ItemVendaPost(1L, 1L, 1, 0.0)));
        assertEquals("Venda não encontrada", exception.getMessage());
    }
    @Test
    void testCriaVendaRealizada() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaRealizada));
        GlobalExceptionHandler.UnprocessableException exception = assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> itemVendaService.cria(new ItemVendaPost(1L, 1L, 1, 0.0)));
        assertEquals("Venda já finalizada", exception.getMessage());
    }
    @Test
    void testCriaProdutoNaoEncontrado() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        GlobalExceptionHandler.NotFoundException exception = assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> itemVendaService.cria(new ItemVendaPost(1L, 1L, 1, 0.0)));
        assertEquals("Produto não encontrado", exception.getMessage());
    }
    @Test
    void testCriaQuantidadeMaiorQueEstoque() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(produto));
        GlobalExceptionHandler.UnprocessableException exception = assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> itemVendaService.cria(new ItemVendaPost(1L, 1L, 11, 0.0)));
        assertEquals("Quantidade maior que o estoque", exception.getMessage());
    }
    @Test
    void testCriaProdutoSemPreco() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(new Produto(1L, "Produto A", null, 10)));
        GlobalExceptionHandler.UnprocessableException exception = assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> itemVendaService.cria(new ItemVendaPost(1L, 1L, 1, 0.0)));
        assertEquals("Produto sem preço", exception.getMessage());
    }
    @Test
    void testCriaDescontoMaiorQuePreco() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(produto));
        GlobalExceptionHandler.BadRequestException exception = assertThrows(GlobalExceptionHandler.BadRequestException.class, () -> itemVendaService.cria(new ItemVendaPost(1L, 1L, 1, 11.0)));
        assertEquals("O desconto deve ser menor ou igual ao preço", exception.getMessage());
    }
    static Stream<Arguments> providerItemVendaPostInvalido() {
        return Stream.of(
                Arguments.of(
                        new ItemVendaPost(1L, 1L, 0, 0.0),
                        new GlobalExceptionHandler.BadRequestException("A quantidade deve ser maior ou igual a 1")
                ),
                Arguments.of(
                        new ItemVendaPost(1L, 1L, 1, -1.0),
                        new GlobalExceptionHandler.BadRequestException("O desconto deve ser maior ou igual a 0")
                ),
                Arguments.of(
                        new ItemVendaPost(null, 1L, 1, 0.0),
                        new GlobalExceptionHandler.BadRequestException("O id da venda não pode ser nulo")
                ),
                Arguments.of(
                        new ItemVendaPost(1L, null, 1, 0.0),
                        new GlobalExceptionHandler.BadRequestException("O id do produto não pode ser nulo")
                )
        );
    }
    @ParameterizedTest
    @MethodSource("providerItemVendaPostInvalido")
    void testCriaInvalido(ItemVendaPost itemVendaPost, RuntimeException exception) {
        assertThrows(exception.getClass(), () -> itemVendaService.cria(itemVendaPost));
    }
    @Test
    void testLista() {
        when(itemVendaRepository.findAll()).thenReturn(java.util.List.of(itemVenda));
        assertEquals(ItemVendaMapper(itemVenda), itemVendaService.lista().get(0));
        assertEquals(1, itemVendaService.lista().size());
    }
    @Test
    void testBusca() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        assertEquals(ItemVendaMapper(itemVenda), itemVendaService.busca(1L));
    }
    @Test
    void testListaPorVenda() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(itemVendaRepository.findAllByVenda_id(1L)).thenReturn(java.util.List.of(itemVenda));
        assertEquals(ItemVendaMapper(itemVenda), itemVendaService.listaPorVenda(1L).get(0));
        assertEquals(1, itemVendaService.listaPorVenda(1L).size());
    }
    @Test
    void testListaPorVendaNaoEncontrada() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        GlobalExceptionHandler.NotFoundException exception = assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> itemVendaService.listaPorVenda(1L));
        assertEquals("Venda não encontrada", exception.getMessage());
    }
    @Test
    void testDeleta() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        itemVendaService.deleta(1L);
    }
    @Test
    void testDeletaNaoEncontrado() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        GlobalExceptionHandler.NotFoundException exception = assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> itemVendaService.deleta(1L));
        assertEquals("Item de venda não encontrado", exception.getMessage());
    }
    @Test
    void testDeletaPorVenda() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        itemVendaService.deletaPorVenda(1L);
    }
    @Test
    void testDeletaPorVendaNaoEncontrada() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        GlobalExceptionHandler.NotFoundException exception = assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> itemVendaService.deletaPorVenda(1L));
        assertEquals("Venda não encontrada", exception.getMessage());
    }
    @Test
    void testDeletaPorVendaRealizada() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaRealizada));
        GlobalExceptionHandler.UnprocessableException exception = assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> itemVendaService.deletaPorVenda(1L));
        assertEquals("Venda já finalizada", exception.getMessage());
    }
    @Test
    void testAtualizaQuantidade() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(produto));
        when(itemVendaRepository.save(any(ItemVenda.class))).thenReturn(itemVenda);
        assertEquals(ItemVendaMapper(itemVenda), itemVendaService.atualizaQuantidade(1L, 1));
    }
    @Test
    void testAtualizaQuantidadeNaoEncontrado() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        GlobalExceptionHandler.NotFoundException exception = assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> itemVendaService.atualizaQuantidade(1L, 1));
        assertEquals("Item de venda não encontrado", exception.getMessage());
    }
    @Test
    void testAtualizaQuantidadeVendaNaoEncontrada() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        GlobalExceptionHandler.NotFoundException exception = assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> itemVendaService.atualizaQuantidade(1L, 1));
        assertEquals("Venda não encontrada", exception.getMessage());
    }
    @Test
    void testAtualizaQuantidadeVendaRealizada() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaRealizada));
        GlobalExceptionHandler.UnprocessableException exception = assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> itemVendaService.atualizaQuantidade(1L, 1));
        assertEquals("Venda já finalizada", exception.getMessage());
    }
    @Test
    void testAtualizaQuantidadeProdutoNaoEncontrado() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        GlobalExceptionHandler.NotFoundException exception = assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> itemVendaService.atualizaQuantidade(1L, 1));
        assertEquals("Produto não encontrado", exception.getMessage());
    }
    @Test
    void testAtualizaQuantidadeQuantidadeMaiorQueEstoque() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(produto));
        GlobalExceptionHandler.UnprocessableException exception = assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> itemVendaService.atualizaQuantidade(1L, 11));
        assertEquals("Quantidade maior que o estoque", exception.getMessage());
    }
    @Test
    void testAtualizaDesconto() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(produto));
        when(itemVendaRepository.save(any(ItemVenda.class))).thenReturn(itemVenda);
        assertEquals(ItemVendaMapper(itemVenda), itemVendaService.atualizaDesconto(1L, 1.0));
    }
    @Test
    void testAtualizaDescontoNaoEncontrado() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        GlobalExceptionHandler.NotFoundException exception = assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> itemVendaService.atualizaDesconto(1L, 1.0));
        assertEquals("Item de venda não encontrado", exception.getMessage());
    }
    @Test
    void testAtualizaDescontoVendaNaoEncontrada() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        GlobalExceptionHandler.NotFoundException exception = assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> itemVendaService.atualizaDesconto(1L, 1.0));
        assertEquals("Venda não encontrada", exception.getMessage());
    }
    @Test
    void testAtualizaDescontoVendaRealizada() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaRealizada));
        GlobalExceptionHandler.UnprocessableException exception = assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> itemVendaService.atualizaDesconto(1L, 1.0));
        assertEquals("Venda já finalizada", exception.getMessage());
    }
    @Test
    void testAtualizaDescontoProdutoNaoEncontrado() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        GlobalExceptionHandler.NotFoundException exception = assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> itemVendaService.atualizaDesconto(1L, 1.0));
        assertEquals("Produto não encontrado", exception.getMessage());
    }
    @Test
    void testAtualizaDescontoProdutoSemPreco() {
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(new Produto(1L, "Produto A", null, 10)));
        GlobalExceptionHandler.UnprocessableException exception = assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> itemVendaService.atualizaDesconto(1L, 1.0));
        assertEquals("Produto sem preço", exception.getMessage());
    }
    @Test
    void testAtualizaDescontoDescontoMaiorQuePreco() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(vendaEmAberto));
        when(produtoRepository.findById(1L)).thenReturn(java.util.Optional.of(produto));
        when(itemVendaRepository.findById(1L)).thenReturn(java.util.Optional.of(itemVenda));
        GlobalExceptionHandler.BadRequestException exception = assertThrows(GlobalExceptionHandler.BadRequestException.class, () -> itemVendaService.atualizaDesconto(1L, 11.0));
        assertEquals("O desconto deve ser menor ou igual ao preço", exception.getMessage());
    }

//    static Stream<Arguments> providerItemVendaPostInvalido() {
//        return Stream.of(
//                Arguments.of(
//                        Named.of(
//                                new ItemVendaPost(1L, 1L, 0, 0.0),
//                        )
//                )
//        );
//    }
//    @ParameterizedTest
//    @MethodSource("providerItemVendaPostInvalido")
//    void testCriaInvalido(ClientePost clientePost, RuntimeException exception) {
}
