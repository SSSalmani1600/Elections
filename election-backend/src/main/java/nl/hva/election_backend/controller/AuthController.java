package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.LoginRequest;
import nl.hva.election_backend.dto.LoginResponse;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.service.AuthService;
import nl.hva.election_backend.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        if (req.getEmail().isEmpty() || req.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email or password");
        }

        User user = authService.authenticate(req.getEmail(), req.getPassword());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        String displayName = user.getDisplayName();
        String token = jwtService.generateToken(user.getId().toString());

        return ResponseEntity.ok(new LoginResponse(token, displayName));
    }
}
