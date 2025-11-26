package nl.hva.election_backend.controller;

import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.security.AdminOnly;
import nl.hva.election_backend.service.ModerationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/moderation")
public class AdminModerationController {

    private final ModerationService moderationService;

    public AdminModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @AdminOnly
    @GetMapping("/pending")
    public List<ReactionEntity> getPending() {
        return moderationService.getPendingReactions();
    }

    @AdminOnly
    @GetMapping("/flagged")
    public List<ReactionEntity> getFlagged() {
        return moderationService.getFlaggedReactions();
    }

    @AdminOnly
    @PostMapping("/{id}/approve")
    public void approve(@PathVariable Long id) {
        moderationService.approveReaction(id);
    }

    @AdminOnly
    @PostMapping("/{id}/reject")
    public void reject(@PathVariable Long id) {
        moderationService.rejectReaction(id);
    }

    @AdminOnly
    @PostMapping("/{id}/flag")
    public void flag(@PathVariable Long id, @RequestBody FlagRequest request) {
        moderationService.flagReaction(id, request.reason());
    }

    public record FlagRequest(String reason) {}
}
