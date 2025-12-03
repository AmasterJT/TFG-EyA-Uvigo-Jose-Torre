package uvigo.tfgalmacen.almacenapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uvigo.tfgalmacen.almacenapi.dto.ActualizarEstadoProductoPedidoRequest;
import uvigo.tfgalmacen.almacenapi.dto.ActualizarPaletizadoRequest;
import uvigo.tfgalmacen.almacenapi.model.DetallePedido;
import uvigo.tfgalmacen.almacenapi.repository.DetallePedidoRepository;

@RestController
@RequestMapping("/api/detalles-pedido")
public class DetallePedidoController {

    private final DetallePedidoRepository detallePedidoRepository;

    public DetallePedidoController(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    // 4) Modificar estado_producto_pedido
    // PATCH /api/detalles-pedido/15/estado-producto
    @PatchMapping("/{idDetalle}/estado-producto")
    public ResponseEntity<DetallePedido> actualizarEstadoProducto(
            @PathVariable Integer idDetalle,
            @RequestBody ActualizarEstadoProductoPedidoRequest request) {

        return detallePedidoRepository.findById(idDetalle)
                .map(detalle -> {
                    detalle.setEstadoProductoPedido(request.getEstadoProductoPedido());
                    DetallePedido guardado = detallePedidoRepository.save(detalle);
                    return ResponseEntity.ok(guardado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5) Modificar paletizado
    // PATCH /api/detalles-pedido/15/paletizado
    @PatchMapping("/{idDetalle}/paletizado")
    public ResponseEntity<DetallePedido> actualizarPaletizado(
            @PathVariable Integer idDetalle,
            @RequestBody ActualizarPaletizadoRequest request) {

        return detallePedidoRepository.findById(idDetalle)
                .map(detalle -> {
                    detalle.setPaletizado(request.getPaletizado());
                    DetallePedido guardado = detallePedidoRepository.save(detalle);
                    return ResponseEntity.ok(guardado);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
