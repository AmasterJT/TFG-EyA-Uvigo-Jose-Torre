package uvigo.tfgalmacen.almacenapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uvigo.tfgalmacen.almacenapi.dto.LoginRequest;
import uvigo.tfgalmacen.almacenapi.dto.LoginResponse;
import uvigo.tfgalmacen.almacenapi.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse resp = service.login(request.getUserName(), request.getPassword());

        if (!resp.isSuccess()) {
            return ResponseEntity.status(401).body(resp);
        }

        return ResponseEntity.ok(resp);
    }
}
