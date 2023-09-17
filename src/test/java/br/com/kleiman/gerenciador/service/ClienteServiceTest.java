package br.com.kleiman.gerenciador.service;

import br.com.kleiman.gerenciador.model.entity.Cliente;
import br.com.kleiman.gerenciador.model.request.ClientePost;
import br.com.kleiman.gerenciador.model.response.ClienteResponse;
import br.com.kleiman.gerenciador.repository.ClienteRepository;
import br.com.kleiman.gerenciador.util.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.stream.Stream;

import static br.com.kleiman.gerenciador.util.GlobalMapper.ClienteMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class ClienteServiceTest {
    @Mock
    private ClienteRepository clienteRepository;
    @InjectMocks
    private ClienteService clienteService;
    private static Cliente cliente = new Cliente(1L, "12345678901", "Cliente A");
    private static ClientePost clientePost = new ClientePost("12345678901", "Cliente A");
    @Test
    void testCria() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        ClienteResponse clienteResponse = clienteService.cria(clientePost);
        assertEquals(ClienteMapper(cliente), clienteResponse);
    }
    static Stream<Arguments> providerClientePostInvalido() {
        return Stream.of(
                Arguments.of(new ClientePost(null, "Cliente A"), new GlobalExceptionHandler.BadRequestException("CPF de cliente é obrigatório")),
                Arguments.of(new ClientePost("", "Cliente A"), new GlobalExceptionHandler.UnprocessableException("CPF não pode ser vazio")),
                Arguments.of(new ClientePost("1234567890", "Cliente A"), new GlobalExceptionHandler.UnprocessableException("CPF deve ter 11 dígitos")),
                Arguments.of(new ClientePost("1234567890a", "Cliente A"), new GlobalExceptionHandler.UnprocessableException("CPF deve conter apenas números")),
                Arguments.of(new ClientePost("12345678901", null), new GlobalExceptionHandler.BadRequestException("Nome de cliente é obrigatório")),
                Arguments.of(new ClientePost("12345678901", ""), new GlobalExceptionHandler.UnprocessableException("Nome não pode ser vazio")),
                Arguments.of(new ClientePost("12345678901", "A"), new GlobalExceptionHandler.UnprocessableException("Nome deve ter pelo menos 3 caracteres")),
                Arguments.of(new ClientePost("12345678901", "A1"), new GlobalExceptionHandler.UnprocessableException("Nome deve conter apenas letras"))
        );
    }
    @ParameterizedTest
    @MethodSource("providerClientePostInvalido")
    void testCriaInvalido(ClientePost clientePost, RuntimeException exception) {
        assertThrows(exception.getClass(), () -> clienteService.cria(clientePost));
    }
    @Test
    void testBusca() {
        when(clienteRepository.findById(1L)).thenReturn(java.util.Optional.of(cliente));
        assertEquals(ClienteMapper(cliente), clienteService.busca(1L));
    }
    @Test
    void testBuscaCpf() {
        when(clienteRepository.findByCpf("12345678901")).thenReturn(java.util.Optional.of(cliente));
        assertEquals(ClienteMapper(cliente), clienteService.buscaCpf("12345678901"));
    }
    @Test
    void testLista() {
        when(clienteRepository.findAll()).thenReturn(java.util.List.of(cliente));
        assertEquals(ClienteMapper(cliente), clienteService.lista().get(0));
        assertEquals(1, clienteService.lista().size());
    }
}
