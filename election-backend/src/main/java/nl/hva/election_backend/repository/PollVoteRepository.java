package nl.hva.election_backend.repository;

import nl.hva.election_backend.model.Poll;
import nl.hva.election_backend.model.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PollVoteRepository extends JpaRepository<PollVote, UUID> {
    boolean existsByPollAndUserId(Poll poll, Long userId);
    List<PollVote> findByPoll(Poll poll);
}
