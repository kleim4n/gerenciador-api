package br.com.kleiman.gerenciador.repository;

import java.util.List;

import br.com.kleiman.gerenciador.model.entity.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
    @Query(value = "select item_venda.* from item_venda where item_venda.venda_id = ?1", nativeQuery = true)
    List<ItemVenda> findAllByVenda_id(long venda_id);
}
