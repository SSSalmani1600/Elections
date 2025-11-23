package nl.hva.election_backend.controller.parser;

import nl.hva.election_backend.entity.PartyEntity;
import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.service.DutchElectionService;
import nl.hva.election_backend.service.PartyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/parties")
public class PartyController {
    private final PartyService partyService;
    DutchElectionService electionService;

    public PartyController(DutchElectionService electionService, PartyService partyService) {
        this.electionService = electionService;
        this.partyService = partyService;
    }

    @GetMapping
    public Set<Party> getParties() {
        return new HashSet<>(electionService.getElection().getParties());
    }
    @GetMapping("/short-info")
    public Set<PartyEntity> readAffiliations() {
        return partyService.getParties();
    }
}
