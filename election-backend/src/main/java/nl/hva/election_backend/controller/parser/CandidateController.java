package nl.hva.election_backend.controller.parser;

import nl.hva.election_backend.model.Candidate;
import nl.hva.election_backend.service.DutchElectionService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("api/parties")
public class CandidateController {
    DutchElectionService electionService;

    public CandidateController(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    @PostMapping("")
    public Set<Candidate> readCandidates(@PathVariable String affiliationId) {
        return electionService.getElection().getPartyByName(affiliationId).getCandidates();
    }
}
