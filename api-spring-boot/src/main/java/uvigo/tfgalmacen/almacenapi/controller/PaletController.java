package uvigo.tfgalmacen.almacenapi.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uvigo.tfgalmacen.almacenapi.dto.ActualizarDimensionesPaletRequest;
import uvigo.tfgalmacen.almacenapi.dto.ActualizarPaletRequest;
import uvigo.tfgalmacen.almacenapi.dto.ActualizarUbicacionPaletRequest;
import uvigo.tfgalmacen.almacenapi.model.Palet;
import uvigo.tfgalmacen.almacenapi.repository.PaletRepository;

import java.util.List;

@RestController
@RequestMapping("/api/palets")
public class PaletController {

    private final PaletRepository paletRepository;

    public PaletController(PaletRepository paletRepository) {
        this.paletRepository = paletRepository;
    }

    // 1) Devuelve todos los palets
    // GET /api/palets
    @GetMapping
    public List<Palet> getAll() {
        return paletRepository.findAll();
    }

    // (Opcional) GET por id
    // GET /api/palets/10
    @GetMapping("/{idPalet}")
    public ResponseEntity<Palet> getById(@PathVariable Integer idPalet) {
        return paletRepository.findById(idPalet)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 2) Endpoint genérico para editar lo que necesites
    // PATCH /api/palets/10
    @PatchMapping("/{idPalet}")
    public ResponseEntity<?> actualizarPalet(
            @PathVariable Integer idPalet,
            @RequestBody ActualizarPaletRequest request) {

        return paletRepository.findById(idPalet)
                .map(palet -> {

                    // actualiza solo lo que venga (null => no tocar)
                    if (request.getIdProducto() != null) palet.setIdProducto(request.getIdProducto());

                    if (request.getAlto() != null) palet.setAlto(request.getAlto());
                    if (request.getAncho() != null) palet.setAncho(request.getAncho());
                    if (request.getLargo() != null) palet.setLargo(request.getLargo());

                    if (request.getCantidadDeProducto() != null) palet.setCantidadDeProducto(request.getCantidadDeProducto());

                    if (request.getEstanteria() != null) palet.setEstanteria(request.getEstanteria());
                    if (request.getBalda() != null) palet.setBalda(request.getBalda());
                    if (request.getPosicion() != null) palet.setPosicion(request.getPosicion());

                    if (request.getDelante() != null) palet.setDelante(request.getDelante());

                    // Validaciones mínimas (opcional, pero útil)
                    if (palet.getAlto() <= 0 || palet.getAncho() <= 0 || palet.getLargo() <= 0) {
                        return ResponseEntity.badRequest().body("Dimensiones deben ser > 0");
                    }
                    if (palet.getCantidadDeProducto() < 0) {
                        return ResponseEntity.badRequest().body("cantidadDeProducto no puede ser negativa");
                    }

                    Palet guardado = paletRepository.save(palet);
                    return ResponseEntity.ok(guardado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //3) Endpoint específico: actualizar dimensiones
    // PATCH /api/palets/10/dimensiones
    @PatchMapping("/{idPalet}/dimensiones")
    public ResponseEntity<?> actualizarDimensiones(
            @PathVariable Integer idPalet,
            @RequestBody ActualizarDimensionesPaletRequest request) {

        if (request.getAlto() == null || request.getAncho() == null || request.getLargo() == null) {
            return ResponseEntity.badRequest().body("alto, ancho y largo son obligatorios");
        }

        if (request.getAlto() <= 0 || request.getAncho() <= 0 || request.getLargo() <= 0) {
            return ResponseEntity.badRequest().body("alto, ancho y largo deben ser > 0");
        }

        return paletRepository.findById(idPalet)
                .map(palet -> {
                    palet.setAlto(request.getAlto());
                    palet.setAncho(request.getAncho());
                    palet.setLargo(request.getLargo());
                    Palet guardado = paletRepository.save(palet);
                    return ResponseEntity.ok(guardado);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // PATCH /api/palets/{idPalet}/ubicacion
    @PatchMapping("/{idPalet}/ubicacion")
    public ResponseEntity<?> actualizarUbicacion(
            @PathVariable Integer idPalet,
            @RequestBody ActualizarUbicacionPaletRequest request) {

        // Validación básica
        if (request.getEstanteria() == null || request.getBalda() == null
                || request.getPosicion() == null || request.getDelante() == null) {
            return ResponseEntity.badRequest().body("Campos obligatorios: estanteria, balda, posicion, delante");
        }

        if (request.getEstanteria() <= 0 || request.getBalda() <= 0 || request.getPosicion() <= 0) {
            return ResponseEntity.badRequest().body("estanteria, balda y posicion deben ser > 0");
        }

        return paletRepository.findById(idPalet)
                .map(palet -> {
                    palet.setEstanteria(request.getEstanteria());
                    palet.setBalda(request.getBalda());
                    palet.setPosicion(request.getPosicion());
                    palet.setDelante(request.getDelante());

                    try {
                        Palet guardado = paletRepository.save(palet);
                        return ResponseEntity.ok(guardado);

                    } catch (DataIntegrityViolationException e) {
                        // Si la uq_ubicacion salta, devolvemos 409
                        if (isUniqueUbicacionViolation(e)) {
                            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                    "Ubicación ocupada (uq_ubicacion): estanteria=" + request.getEstanteria()
                                            + ", balda=" + request.getBalda()
                                            + ", posicion=" + request.getPosicion()
                                            + ", delante=" + request.getDelante()
                            );
                        }
                        // Otra violación de integridad -> 400
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Violación de integridad: " + e.getMostSpecificCause().getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private boolean isUniqueUbicacionViolation(DataIntegrityViolationException e) {
        Throwable cause = e.getMostSpecificCause();
        if (cause == null) return false;
        String msg = cause.getMessage();
        if (msg == null) return false;

        // Esto depende del driver, pero normalmente contiene el nombre del índice/constraint
        return msg.contains("uq_ubicacion") || msg.contains("Duplicate entry");
    }
}
