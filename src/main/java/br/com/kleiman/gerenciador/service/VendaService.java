package br.com.kleiman.gerenciador.service;

import br.com.kleiman.gerenciador.model.entity.Venda;
import br.com.kleiman.gerenciador.model.request.VendaPost;
import br.com.kleiman.gerenciador.model.response.VendaResponse;
import br.com.kleiman.gerenciador.repository.ClienteRepository;
import br.com.kleiman.gerenciador.repository.VendaRepository;
import br.com.kleiman.gerenciador.util.GlobalExceptionHandler;
import br.com.kleiman.gerenciador.util.GlobalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.kleiman.gerenciador.util.GlobalMapper.VendaMapper;

@Service
public class VendaService {
    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    public VendaResponse cria(VendaPost vendaPost) {
        if (vendaPost.cliente_id() != null)
            clienteRepository
                .findById(vendaPost.cliente_id())
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Cliente não encontrado"));
        if (vendaPost.desconto() != null)
            if (vendaPost.desconto() < 0)
                throw new GlobalExceptionHandler.UnprocessableException("Desconto deve ser maior ou igual a zero");
        return VendaMapper(
                vendaRepository.save(
                        new Venda(
                                null,
                                vendaPost.cliente_id(),
                                LocalDateTime.now(),
                                false,
                                vendaPost.desconto()
                        )
                ));
    }

    public VendaResponse finaliza(long id) {
        Venda venda = vendaRepository
                .findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Venda não encontrada"));
        if (venda.getRealizada())
            throw new GlobalExceptionHandler.UnprocessableException("Venda já finalizada");
        return VendaMapper(
                vendaRepository.save(
                        new Venda(
                                venda.getId(),
                                venda.getCliente_id(),
                                LocalDateTime.now(),
                                true,
                                venda.getDesconto()
                        )
                ));
    }

    public VendaResponse cancela(long id) {
        Venda venda = vendaRepository
                .findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Venda não encontrada"));
        if (!venda.getRealizada())
            throw new GlobalExceptionHandler.UnprocessableException("Venda não finalizada");
        return VendaMapper(
                vendaRepository.save(
                        new Venda(
                                venda.getId(),
                                venda.getCliente_id(),
                                LocalDateTime.now(),
                                false,
                                venda.getDesconto()
                        )
                ));
    }

    public List<VendaResponse> lista() {
        return vendaRepository
                .findAll()
                .stream()
                .map(GlobalMapper::VendaMapper)
                .toList();
    }

    public VendaResponse busca(long id) {
        return vendaRepository
                .findById(id)
                .map(GlobalMapper::VendaMapper)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Venda não encontrada"));
    }

    public List<VendaResponse> buscaCliente(String cpf) {
        return vendaRepository
                .findAllByCliente_cpf(cpf)
                .stream()
                .map(GlobalMapper::VendaMapper)
                .toList();
    }

    public void deleta(long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Venda não encontrada"));
        if (venda.getRealizada())
            throw new GlobalExceptionHandler.UnprocessableException("Venda já finalizada - não pode ser excluída");
        vendaRepository.deleteById(id);
    }

    public VendaResponse atualizaDesconto(long id, double desconto) {
        if (desconto < 0)
            throw new GlobalExceptionHandler.UnprocessableException("Desconto deve ser maior ou igual a zero");
        Venda venda = vendaRepository.findById(id).orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Venda não encontrada"));
        if (venda.getRealizada())
            throw new GlobalExceptionHandler.UnprocessableException("Venda já finalizada");
        return VendaMapper(vendaRepository.save(
                new Venda(
                        venda.getId(),
                        venda.getCliente_id(),
                        venda.getData(),
                        venda.getRealizada(),
                        desconto
                )
        ));
    }
}
