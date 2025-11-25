package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.LoginRequest;
import nl.hva.election_backend.dto.LoginResponse;
import nl.hva.election_backend.dto.RegisterRequest;
import nl.hva.election_backend.dto.RegisterResponse;
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid email or password");
        }

        User user = authService.authenticate(req.getEmail(), req.getPassword());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        // UserId als integer
        Integer userId = user.getId().intValue();
        String username = user.getUsername();

        // Token genereren met userId + username
        String token = jwtService.generateToken(userId, username);

        return ResponseEntity.ok(
                new LoginResponse(token, username)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User user = authService.register(req.getEmail(), req.getPassword(), req.getDisplayName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new RegisterResponse(user.getEmail(), req.getPassword(), user.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registratie mislukt");
        }
    }
}
