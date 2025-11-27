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

// REST controller voor gebruikers/account functionaliteit
// Handelt alle HTTP requests af voor gebruikersbeheer en account pagina
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

    // GET /api/users/me - Haalt de huidige ingelogde gebruiker op via JWT token
    // Gebruikt door de account pagina om gebruikersgegevens te tonen
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(value = "jwt", required = false) String accessToken) {
        try {
            // Haal user ID uit JWT token
            String userIdStr = jwtService.extractUserId(accessToken);
            Long userId = Long.parseLong(userIdStr);

            // Zoek gebruiker in database
            Optional<User> user = userRepository.findById(userId);

            // Geef gebruiker terug of 404 als niet gevonden
            return user.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid token: " + e.getMessage());
        }
    }

    // GET /api/users/{id} - Haalt een specifieke gebruiker op op basis van ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT /api/users/{id} - Werkt gebruikersgegevens bij (username, email, wachtwoord)
    // Vereist huidig wachtwoord voor verificatie (veiligheid)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {

        // Zoek gebruiker in database
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // Verificatie: controleer of huidig wachtwoord correct is
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Huidig wachtwoord is verplicht");
        }

        // Vergelijk ingevoerd wachtwoord met opgeslagen hash
        if (!passwordHasher.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Huidig wachtwoord is onjuist");
        }

        // Update username als opgegeven
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername().trim());
        }

        // Update email als opgegeven (met validatie op uniekheid)
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String normalizedEmail = request.getEmail().trim().toLowerCase();

            // Check of email al in gebruik is door een andere gebruiker
            Optional<User> existing = userRepository.findByEmail(normalizedEmail);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("E-mail is al in gebruik");
            }

            user.setEmail(normalizedEmail);
        }

        // Update wachtwoord als opgegeven (met minimale lengte check)
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 8) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Nieuw wachtwoord moet minimaal 8 karakters zijn");
            }

            // Hash het nieuwe wachtwoord voordat je het opslaat
            user.setPasswordHash(passwordHasher.hash(request.getPassword()));
        }

        // Sla aangepaste gebruiker op en geef terug
        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    // GET /api/users/{id}/activity - Haalt alle activiteit van een gebruiker op
    // Retourneert zowel topics (discussies) als reacties die de gebruiker heeft gemaakt
    // Gebruikt door de account pagina om activiteit te tonen
    @GetMapping("/{id}/activity")
    public ResponseEntity<?> getUserActivity(@PathVariable Long id) {

        // Check of gebruiker bestaat
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Haal alle discussies op die door deze gebruiker zijn aangemaakt
        List<DiscussionEntity> discussions = discussionRepository.findByUserIdOrderByCreatedAtDesc(id);
        List<Map<String, Object>> topicsList = new ArrayList<>();

        // Zet discussies om naar een simpel Map formaat voor JSON response
        for (DiscussionEntity d : discussions) {
            Map<String, Object> topic = new HashMap<>();
            topic.put("id", d.getId());
            topic.put("title", d.getTitle());
            topic.put("createdAt", d.getCreatedAt().toString());
            topic.put("reactionsCount", d.getReactionsCount());
            topicsList.add(topic);
        }

        // Haal alle reacties op die door deze gebruiker zijn geschreven
        List<ReactionEntity> reactions = reactionRepository.findByUserIdOrderByCreatedAtDesc(id);
        List<Map<String, Object>> reactionsList = new ArrayList<>();

        // Zet reacties om naar een simpel Map formaat
        for (ReactionEntity r : reactions) {
            Map<String, Object> reaction = new HashMap<>();
            reaction.put("id", r.getId());
            reaction.put("message", r.getMessage());
            reaction.put("createdAt", r.getCreatedAt().toString());
            reaction.put("discussionId", r.getDiscussion().getId());
            reaction.put("discussionTitle", r.getDiscussion().getTitle());
            reactionsList.add(reaction);
        }

        // Combineer topics en reacties in 1 response object
        Map<String, Object> activity = new HashMap<>();
        activity.put("topics", topicsList);
        activity.put("reactions", reactionsList);

        return ResponseEntity.ok(activity);
    }
}
