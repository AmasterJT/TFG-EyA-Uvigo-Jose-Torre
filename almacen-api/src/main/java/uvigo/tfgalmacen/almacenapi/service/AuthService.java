package uvigo.tfgalmacen.almacenapi.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Service;
import uvigo.tfgalmacen.almacenapi.dto.LoginResponse;
import uvigo.tfgalmacen.almacenapi.model.Usuario;
import uvigo.tfgalmacen.almacenapi.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository repo;
    private final Argon2 argon2;

    public AuthService(UsuarioRepository repo) {
        this.repo = repo;
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    }

    public LoginResponse login(String userName, String plainPassword) {

        var opt = repo.findByUserName(userName);

        if (opt.isEmpty()) {
            return LoginResponse.fail("Usuario o contraseña incorrectos");
        }

        Usuario usuario = opt.get();
        String hash = usuario.getPasswordHash();

        if (!argon2.verify(hash, plainPassword.toCharArray())) {
            return LoginResponse.fail("Usuario o contraseña incorrectos");
        }

        return LoginResponse.ok(usuario);
    }
}
