package br.com.kleiman.gerenciador.repository;

import br.com.kleiman.gerenciador.model.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByDescricao(String descricao);
}
