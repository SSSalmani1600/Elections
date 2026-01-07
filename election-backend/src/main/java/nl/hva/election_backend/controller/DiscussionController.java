package nl.hva.election_backend.controller;

// Controller voor discussies/forum

import jakarta.validation.Valid;
import nl.hva.election_backend.dto.CreateDiscussionRequest;
import nl.hva.election_backend.dto.CreateReactionRequest;
import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.PageResponseDto;
import nl.hva.election_backend.dto.ReactionDto;
import nl.hva.election_backend.dto.UpdateDiscussionRequest;
import nl.hva.election_backend.dto.UpdateReactionRequest;
import nl.hva.election_backend.service.DiscussionService;
import nl.hva.election_backend.service.ReactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discussions")
@CrossOrigin
public class DiscussionController {

    private final DiscussionService discussionService;
    private final ReactionService reactionService;

    // constructor injection
    public DiscussionController(DiscussionService discussionService,
                                ReactionService reactionService) {
        this.discussionService = discussionService;
        this.reactionService = reactionService;
    }

    // GET - alle discussies (met optionele paginering)
    @GetMapping
    public ResponseEntity<PageResponseDto<DiscussionListItemDto>> list(
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
        DiscussionDetailDto created = discussionService.createDiscussion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /{id} - discussie bewerken
    @PutMapping("/{id}")
    public ResponseEntity<DiscussionDetailDto> update(@PathVariable Long id, @RequestBody @Valid UpdateDiscussionRequest request) {
        DiscussionDetailDto updated = discussionService.updateDiscussion(id, request);
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
    public ResponseEntity<ReactionDto> addReaction(@PathVariable Long id, @RequestBody @Valid CreateReactionRequest request) {
        ReactionDto saved = reactionService.addReaction(id, request.getUserId(), request.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /reactions/{id} - reactie bewerken
    @PutMapping("/reactions/{reactionId}")
    public ResponseEntity<ReactionDto> updateReaction(@PathVariable Long reactionId, @RequestBody @Valid UpdateReactionRequest request) {
        ReactionDto updated = reactionService.updateReaction(reactionId, request.getUserId(), request.getMessage());
        return ResponseEntity.ok(updated);
    }

    // DELETE /reactions/{id} - reactie verwijderen
    @DeleteMapping("/reactions/{reactionId}")
    public ResponseEntity<Map<String, String>> deleteReaction(@PathVariable Long reactionId, @RequestParam Long userId) {
        reactionService.deleteReaction(reactionId, userId);
        return ResponseEntity.ok(Map.of("message", "Reactie verwijderd"));
    }
}
