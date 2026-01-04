package nl.hva.election_backend.controller;

// Controller voor discussies/forum

import jakarta.validation.Valid;
import nl.hva.election_backend.dto.*;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.exception.ForbiddenException;
import nl.hva.election_backend.service.DiscussionService;
import nl.hva.election_backend.service.ModerationService;
import nl.hva.election_backend.service.ReactionService;
import org.springframework.http.HttpStatus;
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

    // GET - alle discussies (met optionele paginering)
    @GetMapping
    public ResponseEntity<List<DiscussionListItemDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(discussionService.list(page, size));
    }

    // GET /{id} - 1 discussie met details
    @GetMapping("/{id}")
    public ResponseEntity<DiscussionDetailDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(discussionService.getDetailById(id));
    }

    // POST - nieuwe discussie
    @PostMapping
    public ResponseEntity<DiscussionDetailDto> create(@RequestBody @Valid CreateDiscussionRequest request) {
        // moderatie
        ModerationResult modTitle = moderationService.moderateText(request.getTitle());
        ModerationResult modBody = moderationService.moderateText(request.getBody());

        if (modTitle.isBlocked() || modBody.isBlocked()) {
            throw new ForbiddenException("Bericht bevat verboden inhoud.");
        }

        Long newId = discussionService.createDiscussion(
                modTitle.getModeratedText(),
                modBody.getModeratedText(),
                request.getCategory() != null ? request.getCategory() : "algemeen",
                request.getUserId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(discussionService.getDetailById(newId));
    }

    // PUT /{id} - discussie bewerken
    @PutMapping("/{id}")
    public ResponseEntity<DiscussionDetailDto> update(@PathVariable Long id, @RequestBody @Valid UpdateDiscussionRequest request) {
        Long userId = request.getUserId();

        // moderatie
        ModerationResult modTitle = moderationService.moderateText(request.getTitle());
        ModerationResult modBody = moderationService.moderateText(request.getBody());

        if (modTitle.isBlocked() || modBody.isBlocked()) {
            throw new ForbiddenException("Bericht bevat verboden inhoud.");
        }

        DiscussionDetailDto updated = discussionService.updateDiscussion(
                id, userId, modTitle.getModeratedText(), modBody.getModeratedText()
        );
        return ResponseEntity.ok(updated);
    }

    // DELETE /{id} - discussie verwijderen
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id, @RequestParam Long userId) {
        discussionService.deleteDiscussion(id, userId);
        return ResponseEntity.ok(Map.of("message", "Discussie verwijderd"));
    }

    // POST /{id}/reactions - reactie toevoegen
    @PostMapping("/{id}/reactions")
    public ResponseEntity<ReactionEntity> addReaction(@PathVariable Long id, @RequestBody @Valid CreateReactionRequest request) {
        ModerationResult mod = moderationService.moderateText(request.getMessage());
        if (mod.isBlocked()) {
            throw new ForbiddenException("Reactie bevat verboden inhoud.");
        }

        ReactionEntity saved = reactionService.addReaction(id, request.getUserId(), mod.getModeratedText());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /reactions/{id} - reactie bewerken
    @PutMapping("/reactions/{reactionId}")
    public ResponseEntity<ReactionEntity> updateReaction(@PathVariable Long reactionId, @RequestBody @Valid UpdateReactionRequest request) {
        ModerationResult mod = moderationService.moderateText(request.getMessage());
        if (mod.isBlocked()) {
            throw new ForbiddenException("Reactie bevat verboden inhoud.");
        }

        ReactionEntity updated = reactionService.updateReaction(reactionId, request.getUserId(), mod.getModeratedText());
        return ResponseEntity.ok(updated);
    }

    // DELETE /reactions/{id} - reactie verwijderen
    @DeleteMapping("/reactions/{reactionId}")
    public ResponseEntity<Map<String, String>> deleteReaction(@PathVariable Long reactionId, @RequestParam Long userId) {
        reactionService.deleteReaction(reactionId, userId);
        return ResponseEntity.ok(Map.of("message", "Reactie verwijderd"));
    }
}
