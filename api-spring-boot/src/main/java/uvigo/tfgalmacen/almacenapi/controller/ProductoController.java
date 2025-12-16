package uvigo.tfgalmacen.almacenapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uvigo.tfgalmacen.almacenapi.dto.IdentificadorProductoResponse;
import uvigo.tfgalmacen.almacenapi.repository.ProductoRepository;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // GET /api/productos/identificador/12
    @GetMapping("/identificador/{idProducto}")
    public ResponseEntity<IdentificadorProductoResponse> getIdentificadorProducto(
            @PathVariable Integer idProducto) {

        return productoRepository.findById(idProducto)
                .map(producto ->
                        ResponseEntity.ok(
                                new IdentificadorProductoResponse(
                                        producto.getIdentificadorProducto()
                                )
                        )
                )
                .orElse(ResponseEntity.notFound().build());
    }
}
