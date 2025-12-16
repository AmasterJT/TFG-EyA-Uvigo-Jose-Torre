package uvigo.tfgalmacen.almacenapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uvigo.tfgalmacen.almacenapi.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
