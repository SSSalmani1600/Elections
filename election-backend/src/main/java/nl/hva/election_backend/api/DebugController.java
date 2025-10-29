package nl.hva.election_backend.api;

import nl.hva.election_backend.model.Candidate;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.service.DutchElectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DebugController {

    private final DutchElectionService service;

    public DebugController(DutchElectionService service) {
        this.service = service;
    }

    // Voor partijen: stemmen per gemeente
    @GetMapping("/debug/party/municipality")
    public Map<String, Integer> partyMunicipality(
            @RequestParam String electionId,
            @RequestParam String folder,
            @RequestParam String party // naam zoals in AffiliationIdentifier-Id (jullie gebruiken getPartyByName)
    ) {
        Election e = service.readResults(electionId, folder);
        if (e == null) return Map.of();

        Party p = e.getPartyByName(party);
        if (p == null) return Map.of();

        return p.getVotesByMunicipality();
    }

    // Voor partijen: stemmen per stembureau
    @GetMapping("/debug/party/station")
    public Map<String, Integer> partyStation(
            @RequestParam String electionId,
            @RequestParam String folder,
            @RequestParam String party
    ) {
        Election e = service.readResults(electionId, folder);
        if (e == null) return Map.of();

        Party p = e.getPartyByName(party);
        if (p == null) return Map.of();

        return p.getVotesByStation();
    }

    // Voor kandidaten: stemmen per gemeente
    @GetMapping("/debug/candidate/municipality")
    public Map<String, Integer> candidateMunicipality(
            @RequestParam String electionId,
            @RequestParam String folder,
            @RequestParam String fullName // exact zoals in CandidateFullName (bijv. "Pieter-Jan Jansen")
    ) {
        Election e = service.readResults(electionId, folder);
        if (e == null) return Map.of();

        Candidate c = e.getCandidateByName(fullName);
        if (c == null) return Map.of();

        return c.getVotesByMunicipality();
    }

    // Voor kandidaten: stemmen per stembureau
    @GetMapping("/debug/candidate/station")
    public Map<String, Integer> candidateStation(
            @RequestParam String electionId,
            @RequestParam String folder,
            @RequestParam String fullName
    ) {
        Election e = service.readResults(electionId, folder);
        if (e == null) return Map.of();

        Candidate c = e.getCandidateByName(fullName);
        if (c == null) return Map.of();

        return c.getVotesByStation();
    }
}
