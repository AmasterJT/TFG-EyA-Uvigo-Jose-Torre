package uvigo.tfgalmacen.almacenapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uvigo.tfgalmacen.almacenapi.model.Usuario;
import uvigo.tfgalmacen.almacenapi.repository.UsuarioRepository;

import java.util.List;

@SpringBootApplication
public class AlmacenApiApplication {

    public static void main(String[] args) {


        SpringApplication.run(AlmacenApiApplication.class, args);



    }

}
