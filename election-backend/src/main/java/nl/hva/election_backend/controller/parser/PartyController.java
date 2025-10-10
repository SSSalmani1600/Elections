package nl.hva.election_backend.controller.parser;

import nl.hva.election_backend.model.Affiliation;
import nl.hva.election_backend.service.DutchElectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/affiliations")
public class PartyController {
    DutchElectionService electionService;

    public PartyController(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping("/names")
    public Set<String> readAffiliations() {
        return electionService.getElection().getAffiliations().stream().map(Affiliation::getName).collect(Collectors.toSet());
    }
}
