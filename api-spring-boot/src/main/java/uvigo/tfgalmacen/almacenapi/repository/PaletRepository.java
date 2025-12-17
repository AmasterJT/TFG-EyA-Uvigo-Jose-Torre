package uvigo.tfgalmacen.almacenapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uvigo.tfgalmacen.almacenapi.model.Palet;

public interface PaletRepository extends JpaRepository<Palet, Integer> {

    // ¿Hay algún palet (distinto al actual) ocupando esa ubicación?
    boolean existsByEstanteriaAndBaldaAndPosicionAndDelanteAndIdPaletNot(
            Integer estanteria,
            Integer balda,
            Integer posicion,
            Boolean delante,
            Integer idPalet
    );
}
