package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.PollOverviewDto;
import nl.hva.election_backend.dto.PollResult;
import nl.hva.election_backend.model.Poll;
import nl.hva.election_backend.model.PollVote;
import nl.hva.election_backend.repository.PollRepository;
import nl.hva.election_backend.repository.PollVoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        return pollRepository.findAllByOrderByCreatedAtDesc(Pageable.ofSize(1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Geen poll gevonden"
                ));
    }

    public Poll createPoll(String question) {
        return pollRepository.save(new Poll(question.trim()));
    }

    public PollResult vote(UUID pollId, Long userId, String choice) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Stelling bestaat niet"
                ));

        if (voteRepository.existsByPollAndUserId(poll, userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Je hebt al gestemd op deze stelling"
            );
        }

        voteRepository.save(new PollVote(poll, userId, choice));
        return getResults(poll);
    }

    public PollResult getResults(Poll poll) {
        var votes = voteRepository.findByPoll(poll);

        PollResult result = new PollResult();
        result.total = votes.size();
        result.eens = (int) votes.stream().filter(v -> v.getChoice().equals("eens")).count();
        result.oneens = result.total - result.eens;

        return result;
    }

    public PollResult getResults(UUID pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Stelling bestaat niet"
                ));

        return getResults(poll);
    }

    public Page<PollOverviewDto> getAllPollsWithResults(Pageable pageable) {
        return pollRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(poll -> {
                    PollResult r = getResults(poll);
                    int total = r.total;
                    int eens = total == 0 ? 0 : (int) Math.round((r.eens * 100.0) / total);
                    int oneens = total == 0 ? 0 : 100 - eens;

                    return new PollOverviewDto(
                            poll.getId(),
                            poll.getQuestion(),
                            poll.getCreatedAt(),
                            eens,
                            oneens,
                            total
                    );
                });
    }
}
