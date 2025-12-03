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

        return (ResponseEntity<Pedido>) pedidoRepository.findById(idPedido)
                .map(pedido -> {

                    String nuevoEstado = request.getEstado();
                    if (nuevoEstado == null || nuevoEstado.isBlank()) {
                        return ResponseEntity.badRequest().body("El campo 'estado' es obligatorio");
                    }

                    switch (nuevoEstado) {
                        case "Completado":
                            // Regla 1: si es Completado -> hora_salida e id_usuario = null
                            pedido.setEstado("Completado");
                            pedido.setHoraSalida(null);
                            pedido.setIdUsuario(null);
                            break;

                        case "En proceso":
                            // Regla 2: En proceso -> requiere hora_salida + id_usuario
                            if (request.getHoraSalida() == null || request.getIdUsuario() == null) {
                                return ResponseEntity.badRequest()
                                        .body("Para estado 'En proceso' son obligatorios 'horaSalida' e 'idUsuario'");
                            }

                            String hs = request.getHoraSalida();
                            if (!"primera_hora".equals(hs) && !"segunda_hora".equals(hs)) {
                                return ResponseEntity.badRequest()
                                        .body("horaSalida debe ser 'primera_hora' o 'segunda_hora'");
                            }

                            pedido.setEstado("En proceso");
                            pedido.setHoraSalida(hs);
                            pedido.setIdUsuario(request.getIdUsuario());
                            break;

                        default:
                            // Otros estados: solo actualizamos el estado, dejamos lo demás como está
                            pedido.setEstado(nuevoEstado);
                            break;
                    }

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
