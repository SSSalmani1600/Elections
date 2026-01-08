package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.PollResult;
import nl.hva.election_backend.service.JwtService;
import nl.hva.election_backend.service.PollService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/polls")
public class PollController {

    private final PollService pollService;
    private final JwtService jwtService;

    public PollController(PollService pollService, JwtService jwtService) {
        this.pollService = pollService;
        this.jwtService = jwtService;
    }

    public record VoteRequest(String choice) {}

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestPoll() {
        return ResponseEntity.ok(pollService.getLatestPoll());
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<PollResult> vote(
            @PathVariable UUID id,
            @CookieValue(value = "jwt", required = false) String jwt,
            @RequestBody VoteRequest req
    ) {
        if (jwt == null || jwt.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Long userId;
        try {
            userId = Long.parseLong(jwtService.extractUserId(jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PollResult result = pollService.vote(id, userId, req.choice());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<PollResult> getResults(@PathVariable UUID id) {
        return ResponseEntity.ok(pollService.getResults(id));
    }
}
