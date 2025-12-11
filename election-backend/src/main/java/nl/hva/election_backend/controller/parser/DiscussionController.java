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

@RestController
@RequestMapping("/api/discussions")
@CrossOrigin
public class DiscussionController {

    private final DiscussionService discussionService;
    private final ReactionService reactionService;
    private final ModerationService moderationService;

    public DiscussionController(DiscussionService discussionService,
                                ReactionService reactionService,
                                ModerationService moderationService) {
        this.discussionService = discussionService;
        this.reactionService = reactionService;
        this.moderationService = moderationService;
    }
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

     @GetMapping("/{id}")
    public DiscussionDetailDto get(@PathVariable Long id) {
        return discussionService.getDetailById(id);
    }

   @PostMapping
    public ResponseEntity<?> createDiscussion(@RequestBody Map<String, Object> body) {
        try {
            String title = (String) body.get("title");
            String content = (String) body.get("body");
            String category = (String) body.getOrDefault("category", "algemeen");
            Object userIdObj = body.get("userId");

            if (title == null || content == null) {
                return ResponseEntity.badRequest().body("Missing fields");
            }

            if (userIdObj == null) {
                return ResponseEntity.badRequest().body("userId required");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            //  AI moderation for title + body
            ModerationResult modTitle = moderationService.moderateText(title);
            ModerationResult modBody = moderationService.moderateText(content);

            //  Block heavy toxic content
            if (modTitle.isBlocked() || modBody.isBlocked()) {
                return ResponseEntity.status(403).body("Bericht bevat verboden inhoud.");
            }

            // Save sanitized text
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

    @PostMapping("/{id}/reactions")
    public ResponseEntity<?> addReaction(@PathVariable Long id,
                                         @RequestBody Map<String, Object> body) {
        try {
            String message = (String) body.get("message");
            Object userIdObj = body.get("userId");

            if (message == null || userIdObj == null) {
                return ResponseEntity.badRequest().body("Missing fields");
            }

            Long userId = Long.parseLong(userIdObj.toString());

            //  AI moderation
            ModerationResult mod = moderationService.moderateText(message);

            // Block heavy toxicity
            if (mod.isBlocked()) {
                return ResponseEntity.status(403).body("Reactie bevat verboden inhoud.");
            }


            ReactionEntity saved = reactionService.addReaction(id, userId, mod.getModeratedText());

            return ResponseEntity.status(201).body(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error adding reaction: " + e.getMessage());
        }
    }
}
