package nl.hva.election_backend.controller.parser;

import nl.hva.election_backend.model.Constituency;
import nl.hva.election_backend.service.DutchElectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("api/elections/constituencies")
public class ConstituencyController {
    DutchElectionService electionService;

    public ConstituencyController(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public Set<Constituency> getConstituencies() {
        return electionService.getElection().getConstituencies();
    }
}
