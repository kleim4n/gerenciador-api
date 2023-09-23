package br.com.kleiman.gerenciador.service;

import br.com.kleiman.gerenciador.model.entity.Cliente;
import br.com.kleiman.gerenciador.model.entity.Venda;
import br.com.kleiman.gerenciador.model.request.VendaPost;
import br.com.kleiman.gerenciador.model.response.VendaResponse;
import br.com.kleiman.gerenciador.repository.ClienteRepository;
import br.com.kleiman.gerenciador.repository.VendaRepository;
import br.com.kleiman.gerenciador.util.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static br.com.kleiman.gerenciador.util.GlobalMapper.VendaMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class VendaServiceTest {
    @Mock
    private VendaRepository vendaRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @InjectMocks
    private VendaService vendaService;

    Cliente cliente = new Cliente(1L, "12345678901", "Cliente 1");
    Venda venda = new Venda(1L, 1L, null, false, 10.0);
    VendaPost vendaPost = new VendaPost(1L, 10.0);

    @Test
    void testCria() {
        when(clienteRepository.findById(any())).thenReturn(Optional.of(cliente));
        when(vendaRepository.save(any())).thenReturn(venda);
        VendaResponse vendaResponse = vendaService.cria(vendaPost);
        assertEquals(venda.getId(), vendaResponse.id());
        assertEquals(venda.getCliente_id(), vendaResponse.cliente_id());
        assertEquals(venda.getData(), vendaResponse.data());
        assertEquals(venda.getRealizada(), vendaResponse.realizada());
        assertEquals(venda.getDesconto(), vendaResponse.desconto());
    }
    @Test
    void testCriaComClienteIdInexistente() {
        when(clienteRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> vendaService.cria(vendaPost));
    }
    @Test
    void testCriaComDescontoNegativo() {
        assertThrows(GlobalExceptionHandler.UnprocessableException.class, () ->
                vendaService.cria(
                        new VendaPost(
                                null,
                                -1.0
                        )));
    }
    @Test
    void testFinalizaComVendaInexistente() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> vendaService.finaliza(1L));
    }
    @Test
    void testFinalizaComVendaJaFinalizada() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(
                new Venda(
                        1L,
                        1L,
                        null,
                        true,
                        10.0
                )));
        assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> vendaService.finaliza(1L));
    }
    @Test
    void testCancelaComVendaInexistente() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> vendaService.cancela(1L));
    }
    @Test
    void testCancelaComVendaNaoFinalizada() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(
                new Venda(
                        1L,
                        1L,
                        null,
                        false,
                        10.0
                )));
        assertThrows(GlobalExceptionHandler.UnprocessableException.class, () -> vendaService.cancela(1L));
    }
    @Test
    void testLista() {
        when(vendaRepository.findAll()).thenReturn(java.util.List.of(venda));
        assertEquals(java.util.List.of(
                new VendaResponse(
                        venda.getId(),
                        venda.getCliente_id(),
                        venda.getData(),
                        venda.getRealizada(),
                        venda.getDesconto()
                )),
                vendaService.lista());
    }
    @Test
    void testBusca() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(venda));
        assertEquals(
                VendaMapper(venda),
                vendaService.busca(1L));
    }
    @Test
    void testBuscaComVendaInexistente() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> vendaService.busca(1L));
    }
    @Test
    void testBuscaVendasPorCliente() {
        when(vendaRepository.findAllByCliente_cpf("12345678901")).thenReturn(java.util.List.of(venda));
        assertEquals(java.util.List.of(
                new VendaResponse(
                        venda.getId(),
                        venda.getCliente_id(),
                        venda.getData(),
                        venda.getRealizada(),
                        venda.getDesconto()
                )),
                vendaService.buscaCliente("12345678901"));
    }
    @Test
    void testAtualizaDescontoNegativo() {
        assertThrows(GlobalExceptionHandler.UnprocessableException.class, () ->
                vendaService.atualizaDesconto(
                        1L,
                        -1.0
                ));
    }
    @Test
    void testAtualizaComVendaInexistente() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class, () ->
                vendaService.atualizaDesconto(
                        1L,
                        1.0
                ));
    }
    @Test
    void testAtualizaComVendaJaFinalizada() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(
                new Venda(
                        1L,
                        1L,
                        null,
                        true,
                        10.0
                )));
        assertThrows(GlobalExceptionHandler.UnprocessableException.class, () ->
                vendaService.atualizaDesconto(
                        1L,
                        1.0
                ));
    }
    @Test
    void testAtualizaDesconto() {
        when(vendaRepository.findById(1L)).thenReturn(java.util.Optional.of(
                new Venda(
                        1L,
                        1L,
                        null,
                        false,
                        10.0
                )));
        when(vendaRepository.save(any())).thenReturn(
                new Venda(
                        1L,
                        1L,
                        null,
                        false,
                        1.0
                ));
        assertEquals(
                new VendaResponse(
                        1L,
                        1L,
                        null,
                        false,
                        1.0
                ),
                vendaService.atualizaDesconto(
                        1L,
                        1.0
                ));
    }
}
