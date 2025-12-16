package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.VotingGuideAnswerDto;
import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.dto.VotingGuideResponseDto;
import nl.hva.election_backend.dto.VotingGuideResultDto;
import nl.hva.election_backend.entity.PartyViewpointEntity;
import nl.hva.election_backend.entity.VotingGuidePartyEntity;
import nl.hva.election_backend.repository.UserRepository;
import nl.hva.election_backend.repository.VotingGuidePartyRepository;
import nl.hva.election_backend.repository.VotingGuideResultsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    public void givenMultipleMatchingAnswers_whenCalculate_thenReturnSixtyPercent() {
//        GIVEN
        String[] answerArray = {"EENS", "ONEENS", "NEUTRAAL", "ONEENS", "NEUTRAAL"};
        VotingGuideRequestDto userAnswers = new VotingGuideRequestDto(new HashSet<>());
        long statementId = 1L;
        for (String answer : answerArray) {
            userAnswers.getVotingGuideAnswers()
                    .add(new VotingGuideAnswerDto(statementId, answer));
            statementId++;
        }

        String[] partyAnswerArray = {"EENS", "EENS", "NEUTRAAL", "ONEENS", "ONEENS"};
        List<PartyViewpointEntity> partyViewpoints = new ArrayList<>();
        long statementId2 = 1L;
        for (String partyAnswer : partyAnswerArray) {
            partyViewpoints.add(new PartyViewpointEntity(1L, 1L, statementId2, partyAnswer));
            statementId2++;
        }

//        WHEN
        VotingGuideResponseDto response = service.calculate(userAnswers, partyViewpoints);

//        THEN
        VotingGuideResultDto result = response.getVotingGuideResults().getFirst();
        assertEquals(60.0, result.getPercentage());
    }

    @Test
    public void givenTwoPartiesWithDifferentScores_whenCalculate_thenSortByPercentage() {
//        GIVEN
        VotingGuideRequestDto userAnswers = new VotingGuideRequestDto(new HashSet<>());
        userAnswers.getVotingGuideAnswers().add(new VotingGuideAnswerDto(1L, "EENS"));

        List<PartyViewpointEntity> partyViewpoints = new ArrayList<>();
        partyViewpoints.add(new PartyViewpointEntity(1L, 1L, 1L, "NEUTRAAL"));
        partyViewpoints.add(new PartyViewpointEntity(2L, 2L, 1L, "EENS"));

//        WHEN
        VotingGuideResponseDto response = service.calculate(userAnswers, partyViewpoints);

//        THEN
        List<VotingGuideResultDto> result = response.getVotingGuideResults();
        assertEquals(2L, result.getFirst().getPartyId());
        assertEquals(100.0, result.getFirst().getPercentage());

        assertEquals(1L, result.get(1).getPartyId());
        assertEquals(0.0, result.get(1).getPercentage());
    }

    @Test
    public void givenTwoPartiesWithSameScore_whenCalculate_thenSortByPartyId() {
//        GIVEN
        VotingGuideRequestDto userAnswers = new VotingGuideRequestDto(new HashSet<>());
        userAnswers.getVotingGuideAnswers().add(new VotingGuideAnswerDto(1L, "EENS"));

        List<PartyViewpointEntity> partyViewpoints = new ArrayList<>();
        partyViewpoints.add(new PartyViewpointEntity(1L, 1L, 1L, "NEUTRAAL"));
        partyViewpoints.add(new PartyViewpointEntity(2L, 2L, 1L, "EENS"));
        partyViewpoints.add(new PartyViewpointEntity(3L, 3L, 1L, "EENS"));

//        WHEN
        VotingGuideResponseDto response = service.calculate(userAnswers, partyViewpoints);

//        THEN
        List<VotingGuideResultDto> result = response.getVotingGuideResults();
        assertEquals(2L, result.getFirst().getPartyId());
        assertEquals(100.0, result.getFirst().getPercentage());

        assertEquals(3L, result.get(1).getPartyId());
        assertEquals(100.0, result.get(1).getPercentage());
    }

    @Test
    public void givenEmptyAnswers_whenCalculate_thenReturnIllegalArgumentException() {
//        GIVEN
        VotingGuideRequestDto userAnswers = new VotingGuideRequestDto(new HashSet<>());

        List<PartyViewpointEntity> partyViewpoints = new ArrayList<>();
        partyViewpoints.add(new PartyViewpointEntity(1L, 1L, 1L, "NEUTRAAL"));

//        WHEN + THEN
        assertThrows(IllegalArgumentException.class, () ->
                service.calculate(userAnswers, partyViewpoints)
        );
    }

    @Test
    public void givenEmptyParties_whenCalculate_thenReturnIllegalArgumentException() {
//        GIVEN
        VotingGuideRequestDto userAnswers = new VotingGuideRequestDto(new HashSet<>());
        userAnswers.getVotingGuideAnswers().add(new VotingGuideAnswerDto(1L, "EENS"));


        List<PartyViewpointEntity> partyViewpoints = new ArrayList<>();

//        WHEN + THEN
        assertThrows(IllegalArgumentException.class, () ->
                service.calculate(userAnswers, partyViewpoints)
        );
    }
}
