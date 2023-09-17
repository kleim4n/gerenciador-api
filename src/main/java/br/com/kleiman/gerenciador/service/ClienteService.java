package br.com.kleiman.gerenciador.service;

import br.com.kleiman.gerenciador.model.entity.Cliente;
import br.com.kleiman.gerenciador.model.request.ClientePost;
import br.com.kleiman.gerenciador.model.response.ClienteResponse;
import br.com.kleiman.gerenciador.repository.ClienteRepository;
import br.com.kleiman.gerenciador.util.GlobalExceptionHandler;
import br.com.kleiman.gerenciador.util.GlobalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.kleiman.gerenciador.util.GlobalMapper.ClienteMapper;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public List<ClienteResponse> lista() {
        return clienteRepository
                .findAll()
                .stream()
                .map(GlobalMapper::ClienteMapper)
                .toList();
    }

    public ClienteResponse busca(long id) {
        return clienteRepository
                .findById(id)
                .map(GlobalMapper::ClienteMapper)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Cliente não encontrado"));
    }

    public ClienteResponse buscaCpf(String cpf) {
        return clienteRepository
                .findByCpf(cpf)
                .map(GlobalMapper::ClienteMapper)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Cliente não encontrado"));
    }

    public ClienteResponse cria(ClientePost clientePost) {
        if(clientePost.cpf() == null)
            throw new GlobalExceptionHandler.BadRequestException("CPF de cliente é obrigatório");
        if(clientePost.cpf().isBlank())
            throw new GlobalExceptionHandler.UnprocessableException("CPF não pode ser vazio");
        if(clientePost.cpf().length() != 11)
            throw new GlobalExceptionHandler.UnprocessableException("CPF deve ter 11 dígitos");
        if(!clientePost.cpf().matches("[0-9]+"))
            throw new GlobalExceptionHandler.UnprocessableException("CPF deve conter apenas números");
        if(clientePost.nome() == null)
            throw new GlobalExceptionHandler.BadRequestException("Nome de cliente é obrigatório");
        if(clientePost.nome().isBlank())
            throw new GlobalExceptionHandler.UnprocessableException("Nome não pode ser vazio");
        if(clientePost.nome().length() < 3)
            throw new GlobalExceptionHandler.UnprocessableException("Nome deve ter pelo menos 3 caracteres");
        if(!clientePost.nome().matches("[a-zA-Z\\s]+"))
            throw new GlobalExceptionHandler.UnprocessableException("Nome deve conter apenas letras");
        if(clienteRepository.findByCpf(clientePost.cpf()).isPresent())
            throw new GlobalExceptionHandler.UnprocessableException("CPF já cadastrado");
        return ClienteMapper(
                clienteRepository.save(
                        ClienteMapper(clientePost)));
    }

    public void deleta(long id) {
        clienteRepository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Cliente não encontrado"));
        clienteRepository.deleteById(id);
    }

    public ClienteResponse atualizaNome(long id, String nome) {
        if(nome == null)
            throw new GlobalExceptionHandler.BadRequestException("Nome de cliente é obrigatório");
        if(nome.isBlank())
            throw new GlobalExceptionHandler.UnprocessableException("Nome não pode ser vazio");
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Cliente não encontrado"));
        return ClienteMapper(clienteRepository.save(
                        Cliente.builder()
                                .id(cliente.getId())
                                .cpf(cliente.getCpf())
                                .nome(nome)
                                .build()
                ));
    }
}
