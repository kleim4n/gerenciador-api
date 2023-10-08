package br.com.kleiman.gerenciador.service;

import java.util.List;

import br.com.kleiman.gerenciador.model.entity.ItemVenda;
import br.com.kleiman.gerenciador.model.entity.Produto;
import br.com.kleiman.gerenciador.model.request.ItemVendaPost;
import br.com.kleiman.gerenciador.model.response.ItemVendaRespose;
import br.com.kleiman.gerenciador.repository.ItemVendaRepository;
import br.com.kleiman.gerenciador.repository.ProdutoRepository;
import br.com.kleiman.gerenciador.repository.VendaRepository;
import br.com.kleiman.gerenciador.util.GlobalExceptionHandler;
import br.com.kleiman.gerenciador.util.GlobalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemVendaService {
    @Autowired
    private ItemVendaRepository itemVendaRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private VendaRepository vendaRepository;

    public ItemVendaRespose cria(ItemVendaPost itemVendaPost) {
        if (itemVendaPost.quantidade() < 1) {
            throw new GlobalExceptionHandler.BadRequestException("A quantidade deve ser maior ou igual a 1");
        }
        if (itemVendaPost.desconto() < 0) {
            throw new GlobalExceptionHandler.BadRequestException("O desconto deve ser maior ou igual a 0");
        }
        if (itemVendaPost.vendaId() == null) {
            throw new GlobalExceptionHandler.BadRequestException("O id da venda não pode ser nulo");
        }
        if (itemVendaPost.produtoId() == null) {
            throw new GlobalExceptionHandler.BadRequestException("O id do produto não pode ser nulo");
        }
        vendaRepository.findById(itemVendaPost.vendaId())
                .ifPresentOrElse(
                    venda -> {
                        if (venda.getRealizada()) {
                            throw new GlobalExceptionHandler.UnprocessableException("Venda já finalizada");
                        }
                    },
                    () -> {
                        throw new GlobalExceptionHandler.NotFoundException("Venda não encontrada");
                    }
                );
        Produto produto = produtoRepository.findById(itemVendaPost.produtoId())
                .orElseThrow(
                    () -> new GlobalExceptionHandler.NotFoundException("Produto não encontrado")
                );
        if (produto.getEstoque() < itemVendaPost.quantidade()) {
            throw new GlobalExceptionHandler.UnprocessableException("Quantidade maior que o estoque");
        }
        if (produto.getPreco() == null) {
            throw new GlobalExceptionHandler.UnprocessableException("Produto sem preço");
        }
        if (produto.getPreco() < itemVendaPost.desconto()) {
            throw new GlobalExceptionHandler.BadRequestException("O desconto deve ser menor ou igual ao preço");
        }
        ItemVenda itemVenda = GlobalMapper.ItemVendaMapper(itemVendaPost);
        itemVenda.setPreco(produto.getPreco());
        return GlobalMapper.ItemVendaMapper(itemVendaRepository.save(itemVenda));
    }

    public List<ItemVendaRespose> lista() {
        return itemVendaRepository.findAll().stream().map(GlobalMapper::ItemVendaMapper).toList();
    }

    public ItemVendaRespose busca(long id) {
        return itemVendaRepository
                .findById(id)
                .map(GlobalMapper::ItemVendaMapper)
                .orElseThrow(
                    () -> new GlobalExceptionHandler.NotFoundException("Item de venda não encontrado")
                );
    }

    public List<ItemVendaRespose> listaPorVenda(long id) {
        if (vendaRepository.findById(id).isEmpty()) {
            throw new GlobalExceptionHandler.NotFoundException("Venda não encontrada");
        }
        return itemVendaRepository
                .findAllByVenda_id(id)
                .stream()
                .map(GlobalMapper::ItemVendaMapper)
                .toList();
    }

    public void deleta(long id) {
        long vendaId = itemVendaRepository.findById(id)
                .orElseThrow(
                    () -> new GlobalExceptionHandler.NotFoundException("Item de venda não encontrado")
                ).getVenda_id();
        vendaRepository.findById(vendaId)
                .ifPresentOrElse(
                    venda -> {
                        if (venda.getRealizada()) {
                            throw new GlobalExceptionHandler.UnprocessableException("Venda já finalizada");
                        }
                    },
                    () -> {
                        throw new GlobalExceptionHandler.NotFoundException("Venda não encontrada");
                    }
                );
        itemVendaRepository.findById(id)
                .orElseThrow(
                    () -> new GlobalExceptionHandler.NotFoundException("Item de venda não encontrado")
                );
        itemVendaRepository.deleteById(id);
    }

    public void deletaPorVenda(long id) {
        vendaRepository.findById(id)
                .ifPresentOrElse(
                    venda -> {
                        if (venda.getRealizada()) {
                            throw new GlobalExceptionHandler.UnprocessableException("Venda já finalizada");
                        }
                    },
                    () -> {
                        throw new GlobalExceptionHandler.NotFoundException("Venda não encontrada");
                    }
                );
        itemVendaRepository.findAllByVenda_id(id)
                .forEach(
                    itemVenda -> itemVendaRepository.deleteById(itemVenda.getId())
                );
    }

    public ItemVendaRespose atualizaQuantidade(long id, int quantidade) {
        if (quantidade < 1) {
            throw new GlobalExceptionHandler.BadRequestException("A quantidade deve ser maior ou igual a 1");
        }
        ItemVenda itemVenda = itemVendaRepository.findById(id)
                .orElseThrow(
                    () -> new GlobalExceptionHandler.NotFoundException("Item de venda não encontrado")
                );
        vendaRepository.findById(itemVenda.getVenda_id())
                .ifPresentOrElse(
                    venda -> {
                        if (venda.getRealizada()) {
                            throw new GlobalExceptionHandler.UnprocessableException("Venda já finalizada");
                        }
                    },
                    () -> {
                        throw new GlobalExceptionHandler.NotFoundException("Venda não encontrada");
                    }
                );
        Produto produto = produtoRepository.findById(itemVenda.getProduto_id())
                .orElseThrow(
                    () -> new GlobalExceptionHandler.NotFoundException("Produto não encontrado")
                );
        if (produto.getEstoque() < quantidade) {
            throw new GlobalExceptionHandler.UnprocessableException("Quantidade maior que o estoque");
        }
        itemVenda.setQuantidade(quantidade);
        return GlobalMapper.ItemVendaMapper(itemVendaRepository.save(itemVenda));
    }

    public ItemVendaRespose atualizaDesconto(long id, double desconto) {
        if (desconto < 0) {
            throw new GlobalExceptionHandler.BadRequestException("O desconto deve ser maior ou igual a 0");
        }
        ItemVenda itemVenda = itemVendaRepository.findById(id)
                .orElseThrow(
                    () -> new GlobalExceptionHandler.NotFoundException("Item de venda não encontrado")
                );
        vendaRepository.findById(itemVenda.getVenda_id())
                .ifPresentOrElse(
                    venda -> {
                        if (venda.getRealizada()) {
                            throw new GlobalExceptionHandler.UnprocessableException("Venda já finalizada");
                        }
                    },
                    () -> {
                        throw new GlobalExceptionHandler.NotFoundException("Venda não encontrada");
                    }
                );
        Produto produto = produtoRepository.findById(itemVenda.getProduto_id())
                .orElseThrow(
                    () -> new GlobalExceptionHandler.NotFoundException("Produto não encontrado")
                );
        if (produto.getPreco() == null) {
            throw new GlobalExceptionHandler.UnprocessableException("Produto sem preço");
        }
        if (produto.getPreco() < desconto) {
            throw new GlobalExceptionHandler.BadRequestException("O desconto deve ser menor ou igual ao preço");
        }
        itemVenda.setDesconto(desconto);
        return GlobalMapper.ItemVendaMapper(itemVendaRepository.save(itemVenda));
    }

}
