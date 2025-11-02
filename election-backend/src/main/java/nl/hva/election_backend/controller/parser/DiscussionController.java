package nl.hva.election_backend.controller.parser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;


import nl.hva.election_backend.service.DiscussionService;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.model.Discussion;

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

    @PostMapping
    public ResponseEntity<DiscussionDetailDto> create(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // Data
        String title   = body.get("title");
        String content = body.get("body");

        // Author uit JWT (val terug op sub of 'Anonieme gebruiker')
        String author = "Anonieme gebruiker";
        if (jwt != null) {
            String preferred = jwt.getClaimAsString("preferred_username");
            String sub       = jwt.getClaimAsString("sub");
            String resolved  = (preferred != null && !preferred.isBlank()) ? preferred : sub;
            if (resolved != null && !resolved.isBlank()) author = resolved;
        }

        // Validatie
        if (title == null || title.isBlank() || title.length() < 5 || title.length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Titel moet tussen 5 en 120 tekens zijn");
        }
        if (content == null || content.isBlank() || content.length() < 10 || content.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bericht moet tussen 10 en 2000 tekens zijn");
        }

        // Aanmaken + opslaan
        Discussion newDiscussion = Discussion.create(title, author, content);
        service.save(newDiscussion); // <-- via jouw service/repo

        // Terug als DTO
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
}
