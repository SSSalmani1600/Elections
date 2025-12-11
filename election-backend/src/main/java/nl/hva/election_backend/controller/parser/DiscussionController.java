package nl.hva.election_backend.controller.parser;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import nl.hva.election_backend.service.DiscussionService;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.ReactionDto;

import java.util.List;
import java.util.Map;

// REST controller voor de forum/discussie pagina
// Handelt alle HTTP requests af voor discussies en reacties
@RestController
@RequestMapping("/api/discussions")
@CrossOrigin
public class DiscussionController {

    private final DiscussionService service;

    // Constructor injection: Spring geeft automatisch de service door
    public DiscussionController(DiscussionService service) {
        this.service = service;
    }

    // GET /api/discussions - Haalt alle discussies op voor de overzichtspagina
    @GetMapping
    public ResponseEntity<?> list() {
        try {
            List<DiscussionListItemDto> discussions = service.list();
            return ResponseEntity.ok(discussions);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error fetching discussions: " + e.getMessage());
        }
    }

    // GET /api/discussions/{id} - Haalt 1 specifieke discussie op met alle details en reacties
    @GetMapping("/{id}")
    public DiscussionDetailDto get(@PathVariable Long id) {
        return service.getDetailById(id);
    }

    // POST /api/discussions - Maakt een nieuwe discussie aan
    @PostMapping
    public ResponseEntity<?> createDiscussion(@RequestBody Map<String, Object> body) {
        try {
            // Haal de data uit de request body
            String title = (String) body.get("title");
            String content = (String) body.get("body");
            String category = (String) body.getOrDefault("category", "algemeen");
            Object userIdObj = body.get("userId");

            // Valideer dat verplichte velden aanwezig zijn
            if (title == null || content == null)
                return ResponseEntity.badRequest().body("Missing fields");

            if (userIdObj == null)
                return ResponseEntity.badRequest().body("userId required");

            Long userId = Long.parseLong(userIdObj.toString());

            // Maak de discussie aan en geef de nieuwe discussie terug
            Long newId = service.createDiscussion(title, content, category, userId);
            return ResponseEntity.status(201).body(service.getDetailById(newId));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error creating discussion: " + e.getMessage());
        }
    }

    // POST /api/discussions/{id}/reactions - Voegt een reactie toe aan een discussie
    @PostMapping("/{id}/reactions")
    public ResponseEntity<?> addReaction(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            // Haal de reactie data uit de request
            String message = (String) body.get("message");
            Object userIdObj = body.get("userId");

            // Valideer dat verplichte velden aanwezig zijn
            if (message == null || userIdObj == null)
                return ResponseEntity.badRequest().body("Missing fields");

            Long userId = Long.parseLong(userIdObj.toString());

            // Voeg de reactie toe (met automatische moderatie check)
            ReactionDto saved = service.addReaction(id, userId, message);
            return ResponseEntity.status(201).body(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error adding reaction: " + e.getMessage());
        }
    }

    // DELETE /api/discussions/reactions/{reactionId} - Verwijdert een reactie (alleen eigen reacties)
    @DeleteMapping("/reactions/{reactionId}")
    public ResponseEntity<?> deleteReaction(
            @PathVariable Long reactionId,
            @RequestBody Map<String, Object> body) {
        try {
            // Haal de userId uit de request body
            Object userIdObj = body.get("userId");
            if (userIdObj == null) {
                return ResponseEntity.badRequest().body("userId is verplicht");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            // Verwijder de reactie (service checkt of gebruiker eigenaar is)
            service.deleteReaction(reactionId, userId);

            return ResponseEntity.ok().body(Map.of("message", "Reactie verwijderd"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error deleting reaction: " + e.getMessage());
        }
    }
}
