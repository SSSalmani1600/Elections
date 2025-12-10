package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.service.JwtService;
import nl.hva.election_backend.service.VotingGuideAnswersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/voting-guide/answers")
public class VotingGuideAnswersController {
    private final JwtService jwtService;
    private final VotingGuideAnswersService votingGuideAnswersService;

    public VotingGuideAnswersController(JwtService jwtService, VotingGuideAnswersService votingGuideAnswersService) {
        this.jwtService = jwtService;
        this.votingGuideAnswersService = votingGuideAnswersService;
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveAnswers(@RequestBody VotingGuideRequestDto dto, @CookieValue(value = "jwt", required = false) String accessToken) {
        try {
            if (accessToken == null || accessToken.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "No authentication token provided"));
            }

            String userIdStr = jwtService.extractUserId(accessToken);
            if (userIdStr == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid or expired authentication token"));
            }

            if (dto == null || dto.getVotingGuideAnswers() == null || dto.getVotingGuideAnswers().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "No answers provided"));
            }

            Long userId = Long.parseLong(userIdStr);

            votingGuideAnswersService.saveAnswers(dto, userId);

            return ResponseEntity.ok(
                    Map.of("message", "Answers successfully saved")
            );
        } catch (
                NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid user ID in token"));
        } catch (
                RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (
                Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred"));
        }
    }
}
