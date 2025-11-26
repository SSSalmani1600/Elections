package nl.hva.election_backend.controller;

import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.service.ModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/moderation")
public class AdminModerationController {

    private final ModerationService moderationService;

    public AdminModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ReactionEntity>> getPending() {
        return ResponseEntity.ok(moderationService.getPendingReactions());
    }

    @GetMapping("/flagged")
    public ResponseEntity<List<ReactionEntity>> getFlagged() {
        return ResponseEntity.ok(moderationService.getFlaggedReactions());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        moderationService.approveReaction(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        moderationService.rejectReaction(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/flag")
    public ResponseEntity<?> flag(@PathVariable Long id, @RequestBody FlagRequest req) {
        moderationService.flagReaction(id, req.reason());
        return ResponseEntity.ok().build();
    }

    public record FlagRequest(String reason) {}
}
