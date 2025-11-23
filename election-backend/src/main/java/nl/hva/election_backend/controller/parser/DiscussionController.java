package nl.hva.election_backend.controller.parser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import nl.hva.election_backend.service.DiscussionService;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.model.Discussion;
import nl.hva.election_backend.dto.ReactionDto;

import java.util.List;
import java.util.Map;

@RestController // zorgt dat dit praat met frontend via API
@RequestMapping("/api/discussions") // basis link voor discussies
@CrossOrigin // laat frontend lokaal praten zonder gezeik
public class DiscussionController {

    private final DiscussionService service; // hier komt logica vandaan

    public DiscussionController(DiscussionService service) {
        this.service = service; // sla service op zodat we 'm kunnen gebruiken
    }

    /**
     * haalt alle discussies op
     */
    @GetMapping
    public ResponseEntity<?> list() {
        try {
            // pakt discussies uit service en maakt er simpele lijst van
            List<DiscussionListItemDto> discussions = service.list().stream()
                    .map(d -> new DiscussionListItemDto(
                            d.getId(), // id van discussie
                            d.getTitle(), // titel
                            d.getAuthor(), // wie het heeft geplaatst
                            d.getLastActivityAt(), // wanneer laatst actief
                            d.getReactionsCount() // hoeveel reacties
                    ))
                    .toList();
            return ResponseEntity.ok(discussions);
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching discussions: " + e.getMessage());
        }
    }

    /**
     * haalt info van 1 discussie op via id
     */
    @GetMapping("/{id}")
    public DiscussionDetailDto get(@PathVariable Long id) {
        try {
            // zoekt discussie met dit id
            return service.getDetailById(id);
        } catch (IllegalArgumentException e) {
            // niks gevonden? dan 404 error
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Discussion not found");
        }
    }

    /**
     * maakt een nieuwe discussie aan
     */
    @PostMapping
    public ResponseEntity<?> createDiscussion(@RequestBody Map<String, Object> body) {
        try {
            String title = (String) body.get("title");
            String content = (String) body.get("body");
            String category = (String) body.get("category");
            Object userIdObj = body.get("userId") != null ? body.get("userId") : body.get("user_id");
            Object authorObj = body.get("author"); // Can be userId or username

            if (title == null || title.isBlank() || content == null || content.isBlank()) {
                return ResponseEntity.badRequest().body("Title and body are required");
            }

            // Determine userId - prefer userId field, fallback to author
            String author;
            if (userIdObj != null) {
                author = userIdObj.toString();
            } else if (authorObj != null) {
                author = authorObj.toString();
            } else {
                return ResponseEntity.badRequest().body("userId or author is required");
            }

            Discussion newDiscussion = Discussion.create(title, author, content);
            Long discussionId = service.save(newDiscussion, category);

            // Fetch the created discussion to return full details
            DiscussionDetailDto dto = service.getDetailById(discussionId);

            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating discussion: " + e.getMessage());
        }
    }

    // ✅ Nieuwe endpoint: reactie toevoegen aan discussie
    @PostMapping("/{id}/reactions")
    public ResponseEntity<?> addReaction(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        try {
            String message = (String) body.get("message");
            Object userIdObj = body.get("userId") != null ? body.get("userId") : body.get("user_id");
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Message is required");
            }
            
            if (userIdObj == null) {
                return ResponseEntity.badRequest().body("userId is required");
            }
            
            Long userId;
            if (userIdObj instanceof Number) {
                userId = ((Number) userIdObj).longValue();
            } else {
                userId = Long.parseLong(userIdObj.toString());
            }

            // reactie opslaan via service
            ReactionDto saved = service.addReaction(id, userId, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding reaction: " + e.getMessage());
        }
    }
    
    // ✅ Update een reactie
    @PutMapping("/reactions/{reactionId}")
    public ResponseEntity<?> updateReaction(
            @PathVariable Long reactionId,
            @RequestBody Map<String, String> body
    ) {
        try {
            String message = body.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Message is required");
            }
            
            ReactionDto updated = service.updateReaction(reactionId, message);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating reaction: " + e.getMessage());
        }
    }
    
    // ✅ Verwijder een reactie
    @DeleteMapping("/reactions/{reactionId}")
    public ResponseEntity<?> deleteReaction(@PathVariable Long reactionId) {
        try {
            service.deleteReaction(reactionId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting reaction: " + e.getMessage());
        }
    }
    
    // ✅ Update een discussie
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscussion(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String title = body.get("title");
            String content = body.get("body");
            String category = body.get("category");
            
            DiscussionDetailDto updated = service.updateDiscussion(id, title, content, category);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating discussion: " + e.getMessage());
        }
    }
    
    // ✅ Verwijder een discussie
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscussion(@PathVariable Long id) {
        try {
            service.deleteDiscussion(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting discussion: " + e.getMessage());
        }
    }
}
