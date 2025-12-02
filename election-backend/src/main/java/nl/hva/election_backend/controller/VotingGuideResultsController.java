package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.service.VotingGuideResultsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/voting-guide/results")
public class VotingGuideResultsController {
    private VotingGuideResultsService votingGuideResultsService;

    public VotingGuideResultsController() {
    }

    @PostMapping("/calculate")
    public void calculateResults(VotingGuideRequestDto votingGuideRequestDto) {

    }
}
