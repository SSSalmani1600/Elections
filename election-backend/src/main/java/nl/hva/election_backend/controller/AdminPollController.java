package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.PollOverviewDto;
import nl.hva.election_backend.model.Poll;
import nl.hva.election_backend.service.PollService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/polls")
public class AdminPollController {

    private final PollService pollService;

    public AdminPollController(PollService pollService) {
        this.pollService = pollService;
    }

    @GetMapping
    public List<PollOverviewDto> getAllPolls() {
        return pollService.getAllPollsWithResults();
    }

    @PostMapping
    public Poll createPoll(@RequestBody CreatePollRequest req) {

        if (req.getQuestion() == null || req.getQuestion().isBlank()) {
            throw new RuntimeException("Vraag mag niet leeg zijn");
        }

        return pollService.createPoll(req.getQuestion());
    }

    public static class CreatePollRequest {
        private String question;

        public String getQuestion() {
            return question;
        }
    }
}
