package nl.hva.election_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import nl.hva.election_backend.dto.UpdateUserRequest;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.repository.TestRepository;
import nl.hva.election_backend.security.BCryptPasswordHasher;
import nl.hva.election_backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class UserController {

    @Autowired
    private TestRepository userRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private ReactionRepository reactionRepository;

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

            String userIdStr = jwtService.extractUsername(token); // Subject bevat user ID
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

    // 🔹 Bewerken van gebruiker (met wachtwoordverificatie)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // ⚠️ Verifieer huidig wachtwoord VOORDAT wijzigingen worden toegestaan
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Huidig wachtwoord is verplicht om wijzigingen door te voeren");
        }

        if (!passwordHasher.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Huidig wachtwoord is onjuist");
        }

        // ✅ Wachtwoord geverifieerd - nu mogen wijzigingen worden doorgevoerd

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
                return ResponseEntity.status(HttpStatus.CONFLICT).body("E-mail is al in gebruik door een ander account");
            }
            user.setEmail(normalizedEmail);
        }

        // Update wachtwoord als nieuw wachtwoord is opgegeven
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 8) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Nieuw wachtwoord moet minimaal 8 karakters zijn");
            }
            String passwordHash = passwordHasher.hash(request.getPassword());
            user.setPasswordHash(passwordHash);
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // 🔹 Ophalen van gebruikersactiviteit (topics en reacties)
    @GetMapping("/{id}/activity")
    public ResponseEntity<?> getUserActivity(@PathVariable Long id) {
        // Check of user bestaat
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Haal discussies op
        List<DiscussionEntity> discussions = discussionRepository.findByUserIdOrderByCreatedAtDesc(id);
        List<Map<String, Object>> topicsList = new ArrayList<>();
        for (DiscussionEntity d : discussions) {
            Map<String, Object> topic = new HashMap<>();
            topic.put("id", d.getId());
            topic.put("title", d.getTitle());
            topic.put("createdAt", d.getCreatedAt().toString());
            topic.put("reactionsCount", d.getReactionsCount());
            topicsList.add(topic);
        }

        // Haal reacties op
        List<ReactionEntity> reactions = reactionRepository.findByUserIdOrderByCreatedAtDesc(id);
        List<Map<String, Object>> reactionsList = new ArrayList<>();
        for (ReactionEntity r : reactions) {
            Map<String, Object> reaction = new HashMap<>();
            reaction.put("id", r.getId());
            reaction.put("message", r.getMessage());
            reaction.put("createdAt", r.getCreatedAt().toString());
            reaction.put("discussionId", r.getDiscussion().getId());
            reaction.put("discussionTitle", r.getDiscussion().getTitle());
            reactionsList.add(reaction);
        }

        // Combineer in response
        Map<String, Object> activity = new HashMap<>();
        activity.put("topics", topicsList);
        activity.put("reactions", reactionsList);

        return ResponseEntity.ok(activity);
    }
}
