package nl.hva.election_backend.controller;

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

// Controller voor account/gebruikersbeheer
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired private TestRepository userRepository;
    @Autowired private DiscussionRepository discussionRepository;
    @Autowired private ReactionRepository reactionRepository;
    @Autowired private BCryptPasswordHasher passwordHasher;
    @Autowired private JwtService jwtService;

    // Haalt ingelogde gebruiker op via JWT cookie
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(value = "jwt", required = false) String accessToken) {
        try {
            String userIdStr = jwtService.extractUserId(accessToken);
            Long userId = Long.parseLong(userIdStr);
            Optional<User> user = userRepository.findById(userId);
            return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        }
    }

    // Haalt gebruiker op via ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update gebruikersgegevens (vereist huidig wachtwoord)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();

        User user = optionalUser.get();

        // Check huidig wachtwoord
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Huidig wachtwoord is verplicht");
        }
        if (!passwordHasher.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Huidig wachtwoord is onjuist");
        }

        // Update username
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername().trim());
        }

        // Update email (check of niet al in gebruik)
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String normalizedEmail = request.getEmail().trim().toLowerCase();
            Optional<User> existing = userRepository.findByEmail(normalizedEmail);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("E-mail is al in gebruik");
            }
            user.setEmail(normalizedEmail);
        }

        // Update wachtwoord (hash opslaan, nooit plain text)
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 8) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nieuw wachtwoord moet minimaal 8 karakters zijn");
            }
            user.setPasswordHash(passwordHasher.hash(request.getPassword()));
        }

        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    // Haalt activiteit op (topics + reacties van gebruiker)
    @GetMapping("/{id}/activity")
    public ResponseEntity<?> getUserActivity(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();

        // Haal topics op
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

        Map<String, Object> activity = new HashMap<>();
        activity.put("topics", topicsList);
        activity.put("reactions", reactionsList);
        return ResponseEntity.ok(activity);
    }
}
