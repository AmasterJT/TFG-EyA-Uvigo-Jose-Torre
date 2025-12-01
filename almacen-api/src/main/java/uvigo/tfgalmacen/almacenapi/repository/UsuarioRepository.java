package uvigo.tfgalmacen.almacenapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uvigo.tfgalmacen.almacenapi.model.Usuario;

import java.util.List;
import java.util.Optional;

/*
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Devuelve solo la columna nombre de todos los usuarios
    @Query("select u.nombre from Usuario u")
    List<String> findAllNombres();
}
*/

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUserName(String userName);
}