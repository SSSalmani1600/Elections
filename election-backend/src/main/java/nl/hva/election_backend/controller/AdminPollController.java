package nl.hva.election_backend.controller;

import jakarta.validation.Valid;
import nl.hva.election_backend.dto.CreatePollRequest;
import nl.hva.election_backend.dto.PollOverviewDto;
import nl.hva.election_backend.model.Poll;
import nl.hva.election_backend.service.PollService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/polls")
public class AdminPollController {

    private final PollService pollService;

    public AdminPollController(PollService pollService) {
        this.pollService = pollService;
    }

    @GetMapping
    public Page<PollOverviewDto> getAllPolls(
            @PageableDefault(size = 10)
            Pageable pageable
    ) {
        return pollService.getAllPollsWithResults(pageable);
    }

    @PostMapping
    public ResponseEntity<Poll> createPoll(
            @Valid @RequestBody CreatePollRequest req
    ) {
        Poll poll = pollService.createPoll(req.question());
        return ResponseEntity.status(HttpStatus.CREATED).body(poll);
    }
}
