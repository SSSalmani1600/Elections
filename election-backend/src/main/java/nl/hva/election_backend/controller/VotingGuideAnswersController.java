package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.VotingGuideAnswerDto;
import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.service.JwtService;
import nl.hva.election_backend.service.VotingGuideAnswersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/voting-guide/answers")
public class VotingGuideAnswersController {
    private final JwtService jwtService;
    private final VotingGuideAnswersService votingGuideAnswersService;

    public VotingGuideAnswersController(JwtService jwtService, VotingGuideAnswersService votingGuideAnswersService) {
        this.jwtService = jwtService;
        this.votingGuideAnswersService = votingGuideAnswersService;
    }

    @PostMapping("/save-answers")
    public ResponseEntity<?> saveAnswers(@CookieValue(value = "jwt", required = false) String accessToken, @RequestBody VotingGuideRequestDto dto) {
        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "No authentication token provided"));
        }

        String userIdStr = jwtService.extractUserId(accessToken);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired token"));
        }

        if (dto == null || dto.getVotingGuideAnswers() == null || dto.getVotingGuideAnswers().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "No answers provided"));
        }

        Long userId = Long.parseLong(userIdStr);

        votingGuideAnswersService.saveAnswers(dto, userId);

        return ResponseEntity.ok(Map.of("message", "Answers successfully saved"));
    }


    @GetMapping("/get-answers")
    public ResponseEntity<?> getAnswers(@CookieValue(value = "jwt", required = false) String accessToken) {
            if (accessToken == null || accessToken.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "No authentication token is provided "));
            }

            String userIdStr = jwtService.extractUserId(accessToken);
            if (userIdStr == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired authentication token"));
            }

            Long userId = Long.parseLong(userIdStr);
            return ResponseEntity.ok(votingGuideAnswersService.getAnswers(userId));
    }
}
