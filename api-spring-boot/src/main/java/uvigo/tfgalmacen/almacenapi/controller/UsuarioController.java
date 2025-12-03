package uvigo.tfgalmacen.almacenapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uvigo.tfgalmacen.almacenapi.model.Usuario;
import uvigo.tfgalmacen.almacenapi.repository.UsuarioRepository;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uvigo.tfgalmacen.almacenapi.dto.UserIdResponse;

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



    // GET /api/usuarios/id/admin
    @GetMapping("/id/{userName}")
    public ResponseEntity<UserIdResponse> getUserIdByUserName(@PathVariable String userName) {

        return repo.findByUserName(userName)
                .map(usuario -> {
                    // 👇 OJO AQUÍ:
                    // Usa getId() o getIdUsuario() según cómo se llame tu atributo en la entidad
                    Integer id = usuario.getId();          // si tu campo es `private Integer id;`
                    // Integer id = usuario.getIdUsuario(); // si tu campo es `private Integer idUsuario;`
                    return ResponseEntity.ok(new UserIdResponse(id));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /*
    // Devuelve solo la lista de nombres
    @GetMapping("/nombres")
    public List<String> listarNombres() {
        return repo.findAllNombres();
    }
    */

}
