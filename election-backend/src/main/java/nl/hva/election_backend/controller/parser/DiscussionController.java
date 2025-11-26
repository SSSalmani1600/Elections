package nl.hva.election_backend.controller.parser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import nl.hva.election_backend.service.DiscussionService;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.ReactionDto;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discussions")
@CrossOrigin
public class DiscussionController {

    private final DiscussionService service;

    public DiscussionController(DiscussionService service) {
        this.service = service;
    }

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

    @GetMapping("/{id}")
    public DiscussionDetailDto get(@PathVariable Long id) {
        return service.getDetailById(id);
    }

    @PostMapping
    public ResponseEntity<?> createDiscussion(@RequestBody Map<String, Object> body) {
        try {
            String title = (String) body.get("title");
            String content = (String) body.get("body");
            String category = (String) body.getOrDefault("category", "algemeen");
            Object userIdObj = body.get("userId");

            if (title == null || content == null)
                return ResponseEntity.badRequest().body("Missing fields");

            if (userIdObj == null)
                return ResponseEntity.badRequest().body("userId required");

            Long userId = Long.parseLong(userIdObj.toString());

            Long newId = service.createDiscussion(title, content, category, userId);
            return ResponseEntity.status(201).body(service.getDetailById(newId));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error creating discussion: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/reactions")
    public ResponseEntity<?> addReaction(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String message = (String) body.get("message");
            Object userIdObj = body.get("userId");

            if (message == null || userIdObj == null)
                return ResponseEntity.badRequest().body("Missing fields");

            Long userId = Long.parseLong(userIdObj.toString());

            ReactionDto saved = service.addReaction(id, userId, message);
            return ResponseEntity.status(201).body(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error adding reaction: " + e.getMessage());
        }
    }

    // ---------------------- UPDATE REACTION ----------------------
    @PutMapping("/reactions/{reactionId}")
    public ResponseEntity<?> updateReaction(
            @PathVariable Long reactionId,
            @RequestBody Map<String, Object> body
    ) {
        try {
            String message = (String) body.get("message");
            Object userIdObj = body.get("userId");

            if (message == null || userIdObj == null)
                return ResponseEntity.badRequest().body("Missing fields");

            Long userId = Long.parseLong(userIdObj.toString());

            ReactionDto updated = service.updateReaction(reactionId, userId, message);
            return ResponseEntity.ok(updated);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error updating reaction: " + e.getMessage());
        }
    }

    // ---------------------- DELETE REACTION ----------------------
    @DeleteMapping("/reactions/{reactionId}")
    public ResponseEntity<?> deleteReaction(
            @PathVariable Long reactionId,
            @RequestBody Map<String, Object> body
    ) {
        try {
            Object userIdObj = body.get("userId");

            if (userIdObj == null)
                return ResponseEntity.badRequest().body("userId required");

            Long userId = Long.parseLong(userIdObj.toString());

            service.deleteReaction(reactionId, userId);
            return ResponseEntity.ok().body("Reactie verwijderd");

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error deleting reaction: " + e.getMessage());
        }
    }
}
