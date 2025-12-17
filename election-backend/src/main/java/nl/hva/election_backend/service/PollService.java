package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.PollResult;
import nl.hva.election_backend.model.Poll;
import nl.hva.election_backend.model.PollVote;
import nl.hva.election_backend.repository.PollRepository;
import nl.hva.election_backend.repository.PollVoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PollService {

    private final PollRepository pollRepository;
    private final PollVoteRepository voteRepository;

    public PollService(PollRepository pollRepository, PollVoteRepository voteRepository) {
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
    }

    public Poll getLatestPoll() {
        return pollRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("Geen poll gevonden"));
    }

    public Poll createPoll(String question) {
        Poll poll = new Poll(question.trim());
        return pollRepository.save(poll);
    }

    public PollResult vote(UUID pollId, Long userId, String choice) {
        if (!pollRepository.existsById(pollId)) {
            throw new RuntimeException("Stelling bestaat niet.");
        }

        if (voteRepository.existsByPollIdAndUserId(pollId, userId)) {
            throw new RuntimeException("Je hebt al gestemd op deze stelling.");
        }

        voteRepository.save(new PollVote(pollId, userId, choice));

        return getResults(pollId);
    }

    public PollResult getUserVote(UUID pollId, Long userId) {
        if (voteRepository.existsByPollIdAndUserId(pollId, userId)) {
            return getResults(pollId);
        }
        return null;
    }

    public PollResult getResults(UUID pollId) {
        List<PollVote> votes = voteRepository.findByPollId(pollId);

        PollResult dto = new PollResult();
        dto.total = votes.size();
        dto.eens = (int) votes.stream().filter(v -> v.getChoice().equals("eens")).count();
        dto.oneens = dto.total - dto.eens;

        return dto;
    }
}
