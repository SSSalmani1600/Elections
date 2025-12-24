package nl.hva.election_backend.controller;

// Controller voor discussies/forum

import jakarta.validation.Valid;
import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.ModerationResult;
import nl.hva.election_backend.dto.UpdateDiscussionRequest;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.service.DiscussionService;
import nl.hva.election_backend.service.ModerationService;
import nl.hva.election_backend.service.ReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discussions")
@CrossOrigin
public class DiscussionController {

    private final DiscussionService discussionService;
    private final ReactionService reactionService;
    private final ModerationService moderationService;

    // constructor injection
    public DiscussionController(DiscussionService discussionService,
                                ReactionService reactionService,
                                ModerationService moderationService) {
        this.discussionService = discussionService;
        this.reactionService = reactionService;
        this.moderationService = moderationService;
    }

    // GET - alle discussies
    @GetMapping
    public ResponseEntity<List<DiscussionListItemDto>> list() {
        return ResponseEntity.ok(discussionService.list());
    }

    // GET /{id} - 1 discussie met details
    @GetMapping("/{id}")
    public ResponseEntity<DiscussionDetailDto> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(discussionService.getDetailById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - nieuwe discussie
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            String title = (String) body.get("title");
            String content = (String) body.get("body");
            String category = (String) body.getOrDefault("category", "algemeen");
            Object userIdObj = body.get("userId");

            if (title == null || content == null || userIdObj == null) {
                return ResponseEntity.badRequest().body("title, body en userId zijn verplicht");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            // moderatie
            ModerationResult modTitle = moderationService.moderateText(title);
            ModerationResult modBody = moderationService.moderateText(content);

            if (modTitle.isBlocked() || modBody.isBlocked()) {
                return ResponseEntity.status(403).body("Bericht bevat verboden inhoud.");
            }

            Long newId = discussionService.createDiscussion(
                    modTitle.getModeratedText(),
                    modBody.getModeratedText(),
                    category,
                    userId
            );

            return ResponseEntity.status(201).body(discussionService.getDetailById(newId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // PUT /{id} - discussie bewerken
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid UpdateDiscussionRequest request) {
        try {
            Long userId = Long.parseLong(request.userId());
            String title = request.title();
            String body = request.body();

            if (title == null || body == null) {
                return ResponseEntity.badRequest().body("title en body zijn verplicht");
            }

            // moderatie
            ModerationResult modTitle = moderationService.moderateText(title);
            ModerationResult modBody = moderationService.moderateText(body);

            if (modTitle.isBlocked() || modBody.isBlocked()) {
                return ResponseEntity.status(403).body("Bericht bevat verboden inhoud.");
            }

            DiscussionDetailDto updated = discussionService.updateDiscussion(
                    id, userId, modTitle.getModeratedText(), modBody.getModeratedText()
            );
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // DELETE /{id} - discussie verwijderen
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Object userIdObj = body.get("userId");
            if (userIdObj == null) {
                return ResponseEntity.badRequest().body("userId is verplicht");
            }

            Long userId = Long.parseLong(userIdObj.toString());
            discussionService.deleteDiscussion(id, userId);
            return ResponseEntity.ok(Map.of("message", "Discussie verwijderd"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // POST /{id}/reactions - reactie toevoegen
    @PostMapping("/{id}/reactions")
    public ResponseEntity<?> addReaction(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String message = (String) body.get("message");
            Object userIdObj = body.get("userId");

            if (message == null || userIdObj == null) {
                return ResponseEntity.badRequest().body("message en userId zijn verplicht");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            ModerationResult mod = moderationService.moderateText(message);
            if (mod.isBlocked()) {
                return ResponseEntity.status(403).body("Reactie bevat verboden inhoud.");
            }

            ReactionEntity saved = reactionService.addReaction(id, userId, mod.getModeratedText());
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // PUT /reactions/{id} - reactie bewerken
    @PutMapping("/reactions/{reactionId}")
    public ResponseEntity<?> updateReaction(@PathVariable Long reactionId, @RequestBody Map<String, Object> body) {
        try {
            Object userIdObj = body.get("userId");
            String message = (String) body.get("message");

            if (userIdObj == null || message == null) {
                return ResponseEntity.badRequest().body("userId en message zijn verplicht");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            ModerationResult mod = moderationService.moderateText(message);
            if (mod.isBlocked()) {
                return ResponseEntity.status(403).body("Reactie bevat verboden inhoud.");
            }

            ReactionEntity updated = reactionService.updateReaction(reactionId, userId, mod.getModeratedText());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // DELETE /reactions/{id} - reactie verwijderen
    @DeleteMapping("/reactions/{reactionId}")
    public ResponseEntity<?> deleteReaction(@PathVariable Long reactionId, @RequestBody Map<String, Object> body) {
        try {
            Object userIdObj = body.get("userId");
            if (userIdObj == null) {
                return ResponseEntity.badRequest().body("userId is verplicht");
            }

            Long userId = Long.parseLong(userIdObj.toString());
            reactionService.deleteReaction(reactionId, userId);
            return ResponseEntity.ok(Map.of("message", "Reactie verwijderd"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
