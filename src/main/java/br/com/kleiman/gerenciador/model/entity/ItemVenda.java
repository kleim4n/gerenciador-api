package br.com.kleiman.gerenciador.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ItemVenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "venda_id")
    private Long venda_id;
    @JoinColumn(name = "produto_id")
    private Long produto_id;
    private Integer quantidade;
    private Double preco;
    private Double desconto;
}
