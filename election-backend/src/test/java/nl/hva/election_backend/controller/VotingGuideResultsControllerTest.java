package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.VotingGuideAnswerDto;
import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.dto.VotingGuideResponseDto;
import nl.hva.election_backend.dto.VotingGuideResultDto;
import nl.hva.election_backend.entity.PartyViewpointEntity;
import nl.hva.election_backend.service.JwtService;
import nl.hva.election_backend.service.PartyViewpointService;
import nl.hva.election_backend.service.VotingGuideResultsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VotingGuideResultsControllerTest {
    @InjectMocks
    private VotingGuideResultsController controller;

    @Mock
    private VotingGuideResultsService votingGuideResultsService;
    @Mock
    private PartyViewpointService partyViewpointService;
    @Mock
    private JwtService jwtService;

    @Test
    public void givenValidRequest_whenCalculateResults_thenReturnServiceResult() {
//        GIVEN
        VotingGuideRequestDto userAnswers = new VotingGuideRequestDto(new HashSet<>());
        userAnswers.getVotingGuideAnswers().add(new VotingGuideAnswerDto(1L, "EENS"));
        userAnswers.getVotingGuideAnswers().add(new VotingGuideAnswerDto(2L, "ONEENS"));

        VotingGuideResponseDto resultsResponse = new VotingGuideResponseDto(new ArrayList<>());
        resultsResponse.getVotingGuideResults().add(new VotingGuideResultDto(1L, "VVD", 100.0));
        resultsResponse.getVotingGuideResults().add(new VotingGuideResultDto(2L, "D66", 50.0));

        List<PartyViewpointEntity> partyViewpoints = new ArrayList<>();

        when(partyViewpointService.getAllPartyViewpoints()).thenReturn(partyViewpoints);
        when(votingGuideResultsService.calculate(userAnswers, partyViewpoints)).thenReturn(resultsResponse);

//        WHEN
        VotingGuideResponseDto response = controller.calculateResults(userAnswers);

//        THEN
        assertSame(resultsResponse, response);
    }

    @Test
    void saveResults_givenNullDto_then400() {
        when(jwtService.extractUserId("token")).thenReturn("5");

        ResponseEntity<?> response = controller.saveResults("token", null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of("message", "No answers provided"), response.getBody());
        verify(jwtService).extractUserId("token");
        verifyNoInteractions(votingGuideResultsService);
    }
}
