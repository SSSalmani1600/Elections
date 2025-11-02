package nl.hva.election_backend.controller.parser;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import nl.hva.election_backend.service.DiscussionService;
import nl.hva.election_backend.dto.DiscussionListItemDto;
import nl.hva.election_backend.dto.DiscussionDetailDto;

import java.util.List;

@RestController // zorgt dat dit praat met frontend via API
@RequestMapping("/api/discussions") // basis link voor discussies
@CrossOrigin // laat frontend lokaal praten zonder gezeik
public class DiscussionController {

    private final DiscussionService service; // hier komt logica vandaan

    public DiscussionController(DiscussionService service) {
        this.service = service; // sla service op zodat we 'm kunnen gebruiken
    }

    /** haalt alle discussies op */
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

    /** haalt info van 1 discussie op via id */
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
}
