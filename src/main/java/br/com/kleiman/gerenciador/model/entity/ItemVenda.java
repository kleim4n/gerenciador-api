package br.com.kleiman.gerenciador.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Size;

@Entity
public class ItemVenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "venda_id")
    private Long venda_id;
    @JoinColumn(name = "produto_id")
    private Long produto_id;
    @Size(min = 1, message = "A quantidade deve ser maior ou igual a 1")
    private Integer quantidade;
    private Double preco;
    private Double desconto;
}
