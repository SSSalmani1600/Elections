package nl.hva.election_backend.controller;

// Controller voor account pagina

import nl.hva.election_backend.dto.UpdateUserRequest;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.service.JwtService;
import nl.hva.election_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    // constructor injection
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // GET /me - huidige user via JWT
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(value = "jwt", required = false) String token) {
        try {
            Long userId = Long.parseLong(jwtService.extractUserId(token));
            return userService.getUserById(userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    // GET /{id} - user ophalen
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /{id} - user updaten
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        try {
            User updated = userService.updateUser(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // GET /{id}/activity - topics en reacties van user
    @GetMapping("/{id}/activity")
    public ResponseEntity<?> getUserActivity(@PathVariable Long id) {
        try {
            Map<String, Object> activity = userService.getUserActivity(id);
            return ResponseEntity.ok(activity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
