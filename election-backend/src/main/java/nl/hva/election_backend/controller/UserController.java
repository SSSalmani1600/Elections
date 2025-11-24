package nl.hva.election_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import nl.hva.election_backend.dto.UpdateUserRequest;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.TestRepository;
import nl.hva.election_backend.security.BCryptPasswordHasher;
import nl.hva.election_backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class UserController {

    @Autowired
    private TestRepository userRepository;

    @Autowired
    private BCryptPasswordHasher passwordHasher;

    @Autowired
    private JwtService jwtService;

    // 🔹 Ophalen van huidige gebruiker via JWT token
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token required");
        }

        try {
            String token = authHeader.substring("Bearer ".length());
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }

            String userIdStr = jwtService.extractDisplayName(token); // Subject bevat user ID
            Long userId = Long.parseLong(userIdStr);
            
            Optional<User> user = userRepository.findById(userId);
            return user.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        }
    }

    // 🔹 Ophalen van 1 gebruiker
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Bewerken van gebruiker
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // Update username als opgegeven
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername().trim());
        }

        // Update email als opgegeven
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String normalizedEmail = request.getEmail().trim().toLowerCase();
            // Check of email al in gebruik is door andere gebruiker
            Optional<User> existingUser = userRepository.findByEmail(normalizedEmail);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("E-mail is al in gebruik");
            }
            user.setEmail(normalizedEmail);
        }

        // Update wachtwoord als opgegeven
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 8) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wachtwoord moet minimaal 8 karakters zijn");
            }
            String passwordHash = passwordHasher.hash(request.getPassword());
            user.setPasswordHash(passwordHash);
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }
}
