package nl.hva.election_backend.controller;

// Controller voor account pagina

import jakarta.validation.Valid;
import nl.hva.election_backend.dto.UpdateUserRequest;
import nl.hva.election_backend.exception.ResourceNotFoundException;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.service.JwtService;
import nl.hva.election_backend.service.UserService;
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
    public ResponseEntity<User> getCurrentUser(@CookieValue(value = "jwt", required = false) String token) {
        Long userId = Long.parseLong(jwtService.extractUserId(token));
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // GET /{id} - user ophalen
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // PUT /{id} - user updaten
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest request) {
        User updated = userService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    // GET /{id}/activity - topics en reacties van user
    @GetMapping("/{id}/activity")
    public ResponseEntity<Map<String, Object>> getUserActivity(@PathVariable Long id) {
        Map<String, Object> activity = userService.getUserActivity(id);
        return ResponseEntity.ok(activity);
    }
}
