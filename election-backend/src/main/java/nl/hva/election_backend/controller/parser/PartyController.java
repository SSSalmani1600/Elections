package nl.hva.election_backend.controller.parser;

import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.service.DutchElectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/parties")
public class PartyController {
    DutchElectionService electionService;

    public PartyController(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping("/names")
    public Set<String> readAffiliations() {
        return electionService.getElection().getParties().stream().map(Party::getName).collect(Collectors.toSet());
    }
}
