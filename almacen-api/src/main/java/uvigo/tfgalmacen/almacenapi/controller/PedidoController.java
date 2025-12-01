package uvigo.tfgalmacen.almacenapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uvigo.tfgalmacen.almacenapi.dto.ActualizarEstadoPedidoRequest;
import uvigo.tfgalmacen.almacenapi.dto.ActualizarPaletsPedidoRequest;
import uvigo.tfgalmacen.almacenapi.model.Pedido;
import uvigo.tfgalmacen.almacenapi.model.DetallePedido;
import uvigo.tfgalmacen.almacenapi.repository.PedidoRepository;
import uvigo.tfgalmacen.almacenapi.repository.DetallePedidoRepository;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    // 1) Devuelve todos los pedidos "En proceso" de un usuario dado
    // GET /api/pedidos/en-proceso/usuario/5
    @GetMapping("/en-proceso/usuario/{idUsuario}")
    public List<Pedido> getPedidosEnProcesoPorUsuario(@PathVariable Integer idUsuario) {
        return pedidoRepository.findByEstadoAndIdUsuario("En proceso", idUsuario);
    }

    // 2) Devuelve los detalles de un pedido dado
    // GET /api/pedidos/10/detalles
    @GetMapping("/{idPedido}/detalles")
    public List<DetallePedido> getDetallesPorPedido(@PathVariable Integer idPedido) {
        return detallePedidoRepository.findByIdPedido(idPedido);
    }

    // 3) Cambiar el estado de un pedido dado
    // PATCH /api/pedidos/10/estado
    @PatchMapping("/{idPedido}/estado")
    public ResponseEntity<Pedido> actualizarEstadoPedido(
            @PathVariable Integer idPedido,
            @RequestBody ActualizarEstadoPedidoRequest request) {

        return pedidoRepository.findById(idPedido)
                .map(pedido -> {
                    pedido.setEstado(request.getEstado());
                    Pedido guardado = pedidoRepository.save(pedido);
                    return ResponseEntity.ok(guardado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 6) Modificar el campo palets_del_pedido
    // PATCH /api/pedidos/10/palets
    @PatchMapping("/{idPedido}/palets")
    public ResponseEntity<Pedido> actualizarPaletsDelPedido(
            @PathVariable Integer idPedido,
            @RequestBody ActualizarPaletsPedidoRequest request) {

        return pedidoRepository.findById(idPedido)
                .map(pedido -> {
                    pedido.setPaletsDelPedido(request.getPaletsDelPedido());
                    Pedido guardado = pedidoRepository.save(pedido);
                    return ResponseEntity.ok(guardado);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
