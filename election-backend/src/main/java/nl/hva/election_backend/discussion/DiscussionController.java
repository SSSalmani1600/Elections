package nl.hva.election_backend.discussion;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/discussions")
@CrossOrigin(origins = "*")
public class DiscussionController {

    // heel simpel: in-memory lijst (reset bij restart)
    private final List<Discussion> discussions = new ArrayList<>();

    @GetMapping
    public List<Discussion> all() {
        return discussions.stream()
                .sorted(Comparator.comparing(Discussion::getLastActivityAt).reversed())
                .toList();
    }

    @GetMapping("/{id}")
    public Discussion one(@PathVariable String id) {
        return discussions.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Not found"));
    }

    @PostMapping
    public Discussion create(@RequestBody Map<String, String> body) {
        String title  = body.getOrDefault("title", "");
        String author = body.getOrDefault("author", "");
        String text   = body.getOrDefault("body", "");
        Discussion d = Discussion.create(title, author, text);
        discussions.add(d);
        return d;
    }
}
