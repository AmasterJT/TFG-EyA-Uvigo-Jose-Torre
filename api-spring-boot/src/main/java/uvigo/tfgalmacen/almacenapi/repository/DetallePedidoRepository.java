package uvigo.tfgalmacen.almacenapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uvigo.tfgalmacen.almacenapi.model.DetallePedido;

import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    // 2) Detalles de un pedido dado
    List<DetallePedido> findByIdPedido(Integer idPedido);
}
