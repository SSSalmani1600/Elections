package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.dto.VotingGuideResponseDto;
import nl.hva.election_backend.entity.PartyViewpointEntity;
import nl.hva.election_backend.service.JwtService;
import nl.hva.election_backend.service.PartyViewpointService;
import nl.hva.election_backend.service.VotingGuideResultsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/voting-guide/results")
public class VotingGuideResultsController {
    private final VotingGuideResultsService votingGuideResultsService;
    private final PartyViewpointService partyViewpointService;
    private final JwtService jwtService;

    public VotingGuideResultsController(VotingGuideResultsService votingGuideResultsService, PartyViewpointService partyViewpointService, JwtService jwtService) {
        this.votingGuideResultsService = votingGuideResultsService;
        this.partyViewpointService = partyViewpointService;
        this.jwtService = jwtService;
    }

    @PostMapping("/calculate")
    public VotingGuideResponseDto calculateResults(@RequestBody VotingGuideRequestDto votingGuideRequestDto) {
        List<PartyViewpointEntity> partyViewpoints = partyViewpointService.getAllPartyViewpoints();
        return votingGuideResultsService.calculate(votingGuideRequestDto, partyViewpoints);
    }

    @PostMapping("/save-results")
    public ResponseEntity<?> saveResults(@CookieValue(value = "jwt", required = false) String accessToken, @RequestBody VotingGuideResponseDto dto) {
        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "No authentication token provided"));
        }

        String userIdStr = jwtService.extractUserId(accessToken);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));
        }

        if (dto == null || dto.getVotingGuideResults() == null || dto.getVotingGuideResults().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "No answers provided"));
        }

        Long userId = Long.parseLong(userIdStr);

        votingGuideResultsService.saveResults(dto, userId);

        return ResponseEntity.ok(Map.of("message", "Answers successfully saved"));
    }

    @GetMapping("/get-results")
    public ResponseEntity<?> getResults(@CookieValue(value = "jwt", required = false) String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "No authentication token is provided "));
        }

        String userIdStr = jwtService.extractUserId(accessToken);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired authentication token"));
        }

        Long userId = Long.parseLong(userIdStr);
        return ResponseEntity.ok(votingGuideResultsService.getResults(userId));
    }
}
