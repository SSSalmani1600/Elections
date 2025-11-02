// Regelt logica tussen controller en repo.
// Zegt tegen repo wat hij moet halen
package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.model.Discussion;
import nl.hva.election_backend.repo.InMemoryDiscussionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // maakt duidelijk dat dit logica laag is
public class DiscussionService {

    private final InMemoryDiscussionRepository repo; // repo waar data vandaan komt

    public DiscussionService(InMemoryDiscussionRepository repo) {
        this.repo = repo; // opslaan zodat we 'm kunnen gebruiken
    }

    // haalt lijst met discussies op
    public List<Discussion> list() {
        return repo.findAll(); // pakt alles uit repo
    }

    // haalt detail van 1 discussie op (met reacties)
    public DiscussionDetailDto getDetailById(String id) {
        // zoekt discussie via id, anders foutmelding
        return repo.findDetailById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found"));
    }
    // nieuw topic opslaan
    public void save(Discussion d) {
        repo.save(d);
    }

}
