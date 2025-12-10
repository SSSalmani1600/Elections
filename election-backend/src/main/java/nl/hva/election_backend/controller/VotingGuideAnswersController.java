package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.VotingGuideAnswerDto;
import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.service.VotingGuideAnswersService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/voting-guide/answers")
public class VotingGuideAnswersController {
    private final VotingGuideAnswersService votingGuideAnswersService;

    public VotingGuideAnswersController(VotingGuideAnswersService votingGuideAnswersService) {
        this.votingGuideAnswersService = votingGuideAnswersService;
    }

    @GetMapping("/save")
    public void saveAnswers(@RequestBody VotingGuideRequestDto votingGuideRequestDto, @RequestBody User user) {
        votingGuideAnswersService.saveAnswers(votingGuideRequestDto, user);
    }
}
