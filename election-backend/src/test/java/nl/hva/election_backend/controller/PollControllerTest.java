package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.PollResult;
import nl.hva.election_backend.model.Poll;
import nl.hva.election_backend.service.JwtService;
import nl.hva.election_backend.service.PollService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PollControllerTest {

    @Mock
    private PollService pollService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private PollController pollController;

    @Test
    void getLatestPoll_ReturnsPoll() {
        Poll poll = new Poll("Test stelling");
        when(pollService.getLatestPoll()).thenReturn(poll);

        ResponseEntity<?> response = pollController.getLatestPoll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(poll, response.getBody());
    }

    @Test
    void vote_Success() {
        UUID pollId = UUID.randomUUID();
        String jwt = "valid-jwt";

        PollResult pollResult = new PollResult();

        when(jwtService.extractUserId(jwt)).thenReturn("1");
        when(pollService.vote(pollId, 1L, "eens")).thenReturn(pollResult);

        ResponseEntity<PollResult> response =
                pollController.vote(pollId, jwt, new PollController.VoteRequest("eens"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(pollResult, response.getBody());
    }

    @Test
    void vote_NoJwt_ReturnsBadRequest() {
        UUID pollId = UUID.randomUUID();

        ResponseEntity<PollResult> response =
                pollController.vote(pollId, null, new PollController.VoteRequest("eens"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void vote_InvalidJwt_ReturnsUnauthorized() {
        UUID pollId = UUID.randomUUID();
        String jwt = "invalid-jwt";

        when(jwtService.extractUserId(jwt)).thenThrow(new RuntimeException());

        ResponseEntity<PollResult> response =
                pollController.vote(pollId, jwt, new PollController.VoteRequest("eens"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getResults_ReturnsPollResult() {
        UUID pollId = UUID.randomUUID();
        PollResult result = new PollResult();

        when(pollService.getResults(pollId)).thenReturn(result);

        ResponseEntity<PollResult> response = pollController.getResults(pollId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(result, response.getBody());
    }
}
