package nl.hva.election_backend.discussion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/discussions")
@CrossOrigin // laat frontend lokaal callen zonder CORS gedoe
public class DiscussionController {

    private final DiscussionService service;

    public DiscussionController(DiscussionService service) {
        this.service = service;
    }

    @GetMapping
    public List<DiscussionListItemDto> list() {
        return service.list().stream()
                .map(d -> new DiscussionListItemDto(
                        d.getId(),
                        d.getTitle(),
                        d.getAuthor(),
                        d.getLastActivityAt(),
                        d.getReactionsCount()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public DiscussionDetailDto get(@PathVariable String id) {
        try {
            var d = service.getById(id);
            return new DiscussionDetailDto(
                    d.getId(),
                    d.getTitle(),
                    d.getAuthor(),
                    d.getBody(),
                    d.getCreatedAt(),
                    d.getLastActivityAt(),
                    d.getReactionsCount()
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Discussion not found");
        }
    }
}
