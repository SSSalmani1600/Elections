package nl.hva.election_backend.controller.parser;

import nl.hva.election_backend.dto.ReactionDto;
import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.ModerationResult;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.service.DiscussionService;
import nl.hva.election_backend.service.ModerationService;
import nl.hva.election_backend.service.ReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// REST controller voor de forum/discussie pagina
// Handelt alle HTTP requests af voor discussies en reacties
@RestController
@RequestMapping("/api/discussions")
@CrossOrigin
public class DiscussionController {

    private final DiscussionService discussionService;
    private final ReactionService reactionService;
    private final ModerationService moderationService;

    // Constructor injection: Spring geeft automatisch de services door
    public DiscussionController(DiscussionService discussionService,
                                ReactionService reactionService,
                                ModerationService moderationService) {
        this.discussionService = discussionService;
        this.reactionService = reactionService;
        this.moderationService = moderationService;
    }

    // GET /api/discussions - Haalt alle discussies op voor de overzichtspagina
    @GetMapping
    public ResponseEntity<?> list() {
        try {
            List<DiscussionListItemDto> discussions = discussionService.list();
            return ResponseEntity.ok(discussions);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error fetching discussions: " + e.getMessage());
        }
    }

    // GET /api/discussions/{id} - Haalt 1 specifieke discussie op met alle details en reacties
    @GetMapping("/{id}")
    public DiscussionDetailDto get(@PathVariable Long id) {
        return discussionService.getDetailById(id);
    }

    // PUT /api/discussions/{id} - Bewerkt een discussie (alleen eigen discussies)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscussion(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            // Haal de data uit de request body
            Object userIdObj = body.get("userId");
            String title = (String) body.get("title");
            String content = (String) body.get("body");

            if (userIdObj == null || title == null || content == null) {
                return ResponseEntity.badRequest().body("userId, title en body zijn verplicht");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            // AI moderation for title + body
            ModerationResult modTitle = moderationService.moderateText(title);
            ModerationResult modBody = moderationService.moderateText(content);

            // Block heavy toxic content
            if (modTitle.isBlocked() || modBody.isBlocked()) {
                return ResponseEntity.status(403).body("Bericht bevat verboden inhoud.");
            }

            // Bewerk de discussie (service checkt of gebruiker eigenaar is)
            DiscussionDetailDto updated = discussionService.updateDiscussion(
                    id,
                    userId,
                    modTitle.getModeratedText(),
                    modBody.getModeratedText()
            );

            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error updating discussion: " + e.getMessage());
        }
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
            if (title == null || content == null) {
                return ResponseEntity.badRequest().body("Missing fields");
            }

            if (userIdObj == null) {
                return ResponseEntity.badRequest().body("userId required");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            // AI moderation for title + body
            ModerationResult modTitle = moderationService.moderateText(title);
            ModerationResult modBody = moderationService.moderateText(content);

            // Block heavy toxic content
            if (modTitle.isBlocked() || modBody.isBlocked()) {
                return ResponseEntity.status(403).body("Bericht bevat verboden inhoud.");
            }

            // Maak de discussie aan met gemodereerde tekst en geef de nieuwe discussie terug
            Long newId = discussionService.createDiscussion(
                    modTitle.getModeratedText(),
                    modBody.getModeratedText(),
                    category,
                    userId
            );

            return ResponseEntity.status(201).body(discussionService.getDetailById(newId));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error creating discussion: " + e.getMessage());
        }
    }

    // POST /api/discussions/{id}/reactions - Voegt een reactie toe aan een discussie
    @PostMapping("/{id}/reactions")
    public ResponseEntity<?> addReaction(@PathVariable Long id,
                                         @RequestBody Map<String, Object> body) {
        try {
            // Haal de reactie data uit de request
            String message = (String) body.get("message");
            Object userIdObj = body.get("userId");

            // Valideer dat verplichte velden aanwezig zijn
            if (message == null || userIdObj == null) {
                return ResponseEntity.badRequest().body("Missing fields");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            // AI moderation
            ModerationResult mod = moderationService.moderateText(message);

            // Block heavy toxicity
            if (mod.isBlocked()) {
                return ResponseEntity.status(403).body("Reactie bevat verboden inhoud.");
            }

            // Voeg de reactie toe met gemodereerde tekst
            ReactionEntity saved = reactionService.addReaction(id, userId, mod.getModeratedText());

            return ResponseEntity.status(201).body(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error adding reaction: " + e.getMessage());
        }
    }

    // PUT /api/discussions/reactions/{reactionId} - Bewerkt een reactie (alleen eigen reacties)
    @PutMapping("/reactions/{reactionId}")
    public ResponseEntity<?> updateReaction(
            @PathVariable Long reactionId,
            @RequestBody Map<String, Object> body) {
        try {
            // Haal de data uit de request body
            Object userIdObj = body.get("userId");
            String message = (String) body.get("message");

            if (userIdObj == null || message == null) {
                return ResponseEntity.badRequest().body("userId en message zijn verplicht");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            // AI moderation
            ModerationResult mod = moderationService.moderateText(message);

            // Block heavy toxicity
            if (mod.isBlocked()) {
                return ResponseEntity.status(403).body("Reactie bevat verboden inhoud.");
            }

            // Bewerk de reactie (service checkt of gebruiker eigenaar is)
            ReactionEntity updated = reactionService.updateReaction(reactionId, userId, mod.getModeratedText());

            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error updating reaction: " + e.getMessage());
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
            reactionService.deleteReaction(reactionId, userId);

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
