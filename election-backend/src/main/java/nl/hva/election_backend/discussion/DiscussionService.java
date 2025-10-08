package nl.hva.election_backend.discussion;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussionService {

    private final InMemoryDiscussionRepository repo;

    public DiscussionService(InMemoryDiscussionRepository repo) {
        this.repo = repo;
    }

    /** Lijst voor de overzichtspagina (al gesorteerd in repo). */
    public List<Discussion> list() {
        return repo.findAll();
    }

    /** Detailpagina; gooit 404-achtige fout als id onbekend. */
    public Discussion getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found: " + id));
    }
}
