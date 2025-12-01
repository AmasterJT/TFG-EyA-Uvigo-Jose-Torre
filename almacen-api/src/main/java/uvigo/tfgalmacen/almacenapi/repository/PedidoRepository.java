package uvigo.tfgalmacen.almacenapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uvigo.tfgalmacen.almacenapi.model.Pedido;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // 1) Pedidos "En proceso" de un usuario dado
    List<Pedido> findByEstadoAndIdUsuario(String estado, Integer idUsuario);
}
