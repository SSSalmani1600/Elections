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

import java.time.Instant;
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
    public List<DiscussionListItemDto> list() {
        // pakt discussies uit service en maakt er simpele lijst van
        return service.list().stream()
                .map(d -> new DiscussionListItemDto(
                        d.getId(), // id van discussie
                        d.getTitle(), // titel
                        d.getAuthor(), // wie het heeft geplaatst
                        d.getLastActivityAt(), // wanneer laatst actief
                        d.getReactionsCount() // hoeveel reacties
                ))
                .toList();
    }

    /**
     * haalt info van 1 discussie op via id
     */
    @GetMapping("/{id}")
    public DiscussionDetailDto get(@PathVariable String id) {
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
    public ResponseEntity<DiscussionDetailDto> createDiscussion(@RequestBody Map<String, String> body) {
        String title = body.get("title");
        String content = body.get("body");
        String author = body.get("author"); // komt nu gewoon vanuit frontend

        if (title == null || title.isBlank() || content == null || content.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Discussion newDiscussion = Discussion.create(title, author, content);
        service.save(newDiscussion);

        DiscussionDetailDto dto = new DiscussionDetailDto(
                newDiscussion.getId(),
                newDiscussion.getTitle(),
                newDiscussion.getAuthor(),
                newDiscussion.getBody(),
                newDiscussion.getCreatedAt(),
                newDiscussion.getLastActivityAt(),
                newDiscussion.getReactionsCount(),
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // ✅ Nieuwe endpoint: reactie toevoegen aan discussie
    @PostMapping("/{id}/reactions")
    public ResponseEntity<ReactionDto> addReaction(
            @PathVariable String id,
            @RequestBody ReactionDto reactionDto
    ) {
        // check of bericht leeg is
        if (reactionDto.getMessage() == null || reactionDto.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // author invullen (voor nu komt dit gewoon vanuit frontend)
        String author = reactionDto.getAuthor() != null
                ? reactionDto.getAuthor()
                : "Anoniem";

        // reactie opslaan via service
        ReactionDto saved = service.addReaction(
                id,
                author,
                reactionDto.getMessage()
        );

        // teruggeven aan frontend
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
