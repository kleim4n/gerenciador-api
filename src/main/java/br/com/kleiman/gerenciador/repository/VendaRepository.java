package br.com.kleiman.gerenciador.repository;

import br.com.kleiman.gerenciador.model.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    @Query(value = "select Venda.* from Venda, Cliente where Venda.cliente_id = Cliente.id and Cliente.cpf = ?1", nativeQuery = true)
    List<Venda> findAllByCliente_cpf(String cpf);
}
