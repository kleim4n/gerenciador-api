package br.com.kleiman.gerenciador.service;

import br.com.kleiman.gerenciador.model.entity.ItemVenda;
import br.com.kleiman.gerenciador.model.entity.Venda;
import br.com.kleiman.gerenciador.model.request.VendaPost;
import br.com.kleiman.gerenciador.model.response.VendaResponse;
import br.com.kleiman.gerenciador.repository.ClienteRepository;
import br.com.kleiman.gerenciador.repository.ItemVendaRepository;
import br.com.kleiman.gerenciador.repository.ProdutoRepository;
import br.com.kleiman.gerenciador.repository.VendaRepository;
import br.com.kleiman.gerenciador.util.GlobalExceptionHandler;
import br.com.kleiman.gerenciador.util.GlobalMapper;
import jakarta.transaction.Transactional;
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
    @Autowired
    private ItemVendaRepository itemVendaRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

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

    @Transactional
    public VendaResponse finaliza(long id) {
        Venda venda = vendaRepository
                .findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Venda não encontrada"));
        if (venda.getRealizada())
            throw new GlobalExceptionHandler.UnprocessableException("Venda já finalizada");
        List<ItemVenda> itens = itemVendaRepository.findAllByVenda_id(id);
        if (itens.isEmpty())
            throw new GlobalExceptionHandler.UnprocessableException("Venda sem itens");
        itens.forEach(item -> {
            produtoRepository.findById(item.getProduto_id())
                    .ifPresentOrElse(
                            produto -> {
                                if (produto.getEstoque() < item.getQuantidade())
                                    throw new GlobalExceptionHandler.UnprocessableException("Estoque insuficiente");
                                produto.setEstoque(produto.getEstoque() - item.getQuantidade());
                                produtoRepository.save(produto);
                            },
                            () -> {
                                throw new GlobalExceptionHandler.NotFoundException("Produto não encontrado");
                            }
                    );
        });
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

    @Transactional
    public VendaResponse cancela(long id) {
        Venda venda = vendaRepository
                .findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Venda não encontrada"));
        if (!venda.getRealizada())
            throw new GlobalExceptionHandler.UnprocessableException("Venda não finalizada");
        List<ItemVenda> itens = itemVendaRepository.findAllByVenda_id(id);
        itens.forEach(item -> {
            produtoRepository.findById(item.getProduto_id())
                    .ifPresentOrElse(
                            produto -> {
                                produto.setEstoque(produto.getEstoque() + item.getQuantidade());
                                produtoRepository.save(produto);
                            },
                            () -> {
                                throw new GlobalExceptionHandler.NotFoundException("Produto não encontrado");
                            }
                    );
        });
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
        if (!itemVendaRepository.findAllByVenda_id(id).isEmpty())
            throw new GlobalExceptionHandler.UnprocessableException("Venda com itens - não pode ser excluída");
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
