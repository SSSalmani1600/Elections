package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.PollResult;
import nl.hva.election_backend.model.Poll;
import nl.hva.election_backend.service.JwtService;
import nl.hva.election_backend.service.PollService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3000"
        },
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/polls")
public class PollController {

    private final PollService pollService;
    private final JwtService jwtService;

    public PollController(PollService pollService, JwtService jwtService) {
        this.pollService = pollService;
        this.jwtService = jwtService;
    }

    record VoteRequest(String choice) {}

    @GetMapping("/latest")
    public Poll getLatestPoll() {
        return pollService.getLatestPoll();
    }

    @PostMapping("/{id}/vote")
    public PollResult vote(
            @PathVariable UUID id,
            @CookieValue(value = "jwt", required = false) String jwt,
            @RequestBody VoteRequest req
    ) {
        if (jwt == null || jwt.isBlank()) {
            throw new RuntimeException("Niet ingelogd");
        }

        String userIdStr;
        try {
            userIdStr = jwtService.extractUserId(jwt);
        } catch (Exception e) {
            throw new RuntimeException("Niet ingelogd");
        }

        Long userId = Long.parseLong(userIdStr);

        return pollService.vote(id, userId, req.choice());
    }

    @GetMapping("/{id}/results")
    public PollResult getResults(@PathVariable UUID id) {
        return pollService.getResults(id);
    }
    @GetMapping("/{id}/my-vote")
    public PollResult myVote(
            @PathVariable UUID id,
            @CookieValue(value = "jwt", required = false) String jwt
    ) {
        if (jwt == null) return null;
        Long userId = Long.parseLong(jwtService.extractUserId(jwt));
        return pollService.getUserVote(id, userId);
    }

}
