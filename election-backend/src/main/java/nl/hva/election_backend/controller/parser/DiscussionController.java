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

    // ---------------- LIST ALL ----------------
    @GetMapping
    public ResponseEntity<?> list() {
        try {
            List<DiscussionListItemDto> discussions = service.list();
            return ResponseEntity.ok(discussions);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching discussions: " + e.getMessage());
        }
    }

    // ---------------- DETAIL ----------------
    @GetMapping("/{id}")
    public DiscussionDetailDto get(@PathVariable Long id) {
        try {
            return service.getDetailById(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Discussion not found");
        }
    }

    // ---------------- CREATE NEW ----------------
    @PostMapping
    public ResponseEntity<?> createDiscussion(@RequestBody Map<String, Object> body) {
        try {
            String title = (String) body.get("title");
            String content = (String) body.get("body");
            String category = (String) body.getOrDefault("category", "algemeen");

            Object userIdObj = body.get("userId");

            if (title == null || title.isBlank() || content == null || content.isBlank()) {
                return ResponseEntity.badRequest().body("Title and body are required");
            }
            if (userIdObj == null) {
                return ResponseEntity.badRequest().body("userId is required");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            Long newId = service.createDiscussion(title, content, category, userId);

            DiscussionDetailDto dto = service.getDetailById(newId);

            return ResponseEntity.status(HttpStatus.CREATED).body(dto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating discussion: " + e.getMessage());
        }
    }

    // ---------------- ADD REACTION ----------------
    @PostMapping("/{id}/reactions")
    public ResponseEntity<?> addReaction(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        try {
            String message = (String) body.get("message");
            Object userIdObj = body.get("userId");

            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Message is required");
            }
            if (userIdObj == null) {
                return ResponseEntity.badRequest().body("userId is required");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            ReactionDto saved = service.addReaction(id, userId, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding reaction: " + e.getMessage());
        }
    }

}
