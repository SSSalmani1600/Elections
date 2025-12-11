package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.dto.VotingGuideResponseDto;
import nl.hva.election_backend.dto.VotingGuideResultDto;
import nl.hva.election_backend.entity.PartyViewpointEntity;
import nl.hva.election_backend.service.PartyViewpointService;
import nl.hva.election_backend.service.VotingGuideResultsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voting-guide/results")
public class VotingGuideResultsController {
    private final VotingGuideResultsService votingGuideResultsService;
    private final PartyViewpointService partyViewpointService;

    public VotingGuideResultsController(VotingGuideResultsService votingGuideResultsService, PartyViewpointService partyViewpointService) {
        this.votingGuideResultsService = votingGuideResultsService;
        this.partyViewpointService = partyViewpointService;
    }

    @PostMapping("/calculate")
    public VotingGuideResponseDto calculateResults(@RequestBody VotingGuideRequestDto votingGuideRequestDto) {
        List<PartyViewpointEntity> partyViewpoints = partyViewpointService.getAllPartyViewpoints();
        return votingGuideResultsService.calculate(votingGuideRequestDto, partyViewpoints);
    }
}
