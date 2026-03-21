package br.com.confeitaria.sweet_manager.domain.repository;

import br.com.confeitaria.sweet_manager.domain.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioEmail(String email);
}