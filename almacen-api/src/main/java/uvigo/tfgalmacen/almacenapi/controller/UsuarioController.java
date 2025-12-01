package uvigo.tfgalmacen.almacenapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uvigo.tfgalmacen.almacenapi.model.Usuario;
import uvigo.tfgalmacen.almacenapi.repository.UsuarioRepository;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository repo;

    public UsuarioController(UsuarioRepository repo) {
        this.repo = repo;
    }

    // Devuelve todos los usuarios (objetos completos)
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return repo.findAll();
    }

    /*
    // Devuelve solo la lista de nombres
    @GetMapping("/nombres")
    public List<String> listarNombres() {
        return repo.findAllNombres();
    }
    */

}
