package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.VotingGuideAnswerDto;
import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.dto.VotingGuideResponseDto;
import nl.hva.election_backend.dto.VotingGuideResultDto;
import nl.hva.election_backend.entity.PartyViewpointEntity;
import nl.hva.election_backend.repository.UserRepository;
import nl.hva.election_backend.repository.VotingGuidePartyRepository;
import nl.hva.election_backend.repository.VotingGuideResultsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class VotingGuideResultsServiceTest {
    private VotingGuideResultsService service;

    @BeforeEach
    void init() {
        this.service = new VotingGuideResultsService(mock(VotingGuidePartyRepository.class), mock(UserRepository.class), mock(VotingGuideResultsRepository.class));
    }

    @Test
    public void givenSingleMatchingAnswer_whenCalculate_thenReturnHundredPercent() {
//        GIVEN
        VotingGuideRequestDto userAnswers = new VotingGuideRequestDto(new HashSet<>());
        userAnswers.getVotingGuideAnswers().add(new VotingGuideAnswerDto(1L, "EENS"));

        List<PartyViewpointEntity> partyViewpoints = new ArrayList<>();
        partyViewpoints.add(new PartyViewpointEntity(1L, 1L, 1L, "EENS"));

//        WHEN
        VotingGuideResponseDto response = service.calculate(userAnswers, partyViewpoints);

//        THEN
        VotingGuideResultDto result = response.getVotingGuideResults().getFirst();
        assertEquals(100.0, result.getPercentage());
    }

    @Test
    public void givenNoMatchingAnswers_whenCalculate_thenReturnZeroPercent() {
//        GIVEN
        VotingGuideRequestDto userAnswers = new VotingGuideRequestDto(new HashSet<>());
        userAnswers.getVotingGuideAnswers().add(new VotingGuideAnswerDto(1L, "ONEENS"));

        List<PartyViewpointEntity> partyViewpoints = new ArrayList<>();
        partyViewpoints.add(new PartyViewpointEntity(1L, 1L, 1L, "EENS"));

//        WHEN
        VotingGuideResponseDto response = service.calculate(userAnswers, partyViewpoints);

//        THEN
        VotingGuideResultDto result = response.getVotingGuideResults().getFirst();
        assertEquals(0.0, result.getPercentage());
    }

}
