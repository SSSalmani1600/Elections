package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.PollResult;
import nl.hva.election_backend.model.Poll;
import nl.hva.election_backend.model.PollVote;
import nl.hva.election_backend.repository.PollRepository;
import nl.hva.election_backend.repository.PollVoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @Mock
    private PollVoteRepository voteRepository;

    @InjectMocks
    private PollService pollService;

    @Test
    void getLatestPoll_Success() {
        Poll poll = new Poll("Test stelling");

        when(pollRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(poll)));

        Poll result = pollService.getLatestPoll();

        assertEquals(poll, result);
    }

    @Test
    void getLatestPoll_NotFound() {
        when(pollRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        assertThrows(ResponseStatusException.class, () ->
                pollService.getLatestPoll()
        );
    }

    @Test
    void vote_Success() {
        UUID pollId = UUID.randomUUID();
        Long userId = 1L;

        Poll poll = new Poll("Test stelling");

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(voteRepository.existsByPollAndUserId(poll, userId)).thenReturn(false);
        when(voteRepository.findByPoll(poll)).thenReturn(
                List.of(new PollVote(poll, userId, "eens"))
        );

        PollResult result = pollService.vote(pollId, userId, "eens");

        assertEquals(1, result.total);
        assertEquals(1, result.eens);
        assertEquals(0, result.oneens);

        verify(voteRepository).save(any(PollVote.class));
    }

    @Test
    void vote_AlreadyVoted_ThrowsConflict() {
        UUID pollId = UUID.randomUUID();
        Long userId = 1L;
        Poll poll = new Poll("Test stelling");

        when(pollRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(voteRepository.existsByPollAndUserId(poll, userId)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () ->
                pollService.vote(pollId, userId, "eens")
        );

        verify(voteRepository, never()).save(any());
    }

    @Test
    void getResults_CalculatesCorrectly() {
        Poll poll = new Poll("Test stelling");

        when(voteRepository.findByPoll(poll)).thenReturn(
                List.of(
                        new PollVote(poll, 1L, "eens"),
                        new PollVote(poll, 2L, "oneens"),
                        new PollVote(poll, 3L, "eens")
                )
        );

        PollResult result = pollService.getResults(poll);

        assertEquals(3, result.total);
        assertEquals(2, result.eens);
        assertEquals(1, result.oneens);
    }
}
