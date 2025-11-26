package nl.hva.election_backend.controller.parser;

import nl.hva.election_backend.model.Constituency;
import nl.hva.election_backend.service.ConstituencyService;
import nl.hva.election_backend.service.DutchElectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("api/electionresults")
public class ConstituencyController {
    private final ConstituencyService constituencyService;

    public ConstituencyController(ConstituencyService constituencyService) {
        this.constituencyService = constituencyService;
    }

    @GetMapping("/{election_id}/constituencies")
    public Set<Constituency> getConstituencies(@PathVariable("election_id") int electionId) {
        return constituencyService.getConstituencies(electionId);
    }
}
