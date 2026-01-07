package nl.hva.election_backend.service;

import jakarta.transaction.Transactional;
import nl.hva.election_backend.dto.VotingGuideAnswerDto;
import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.entity.VotingGuideAnswerEntity;
import nl.hva.election_backend.entity.StatementEntity;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.StatementRepository;
import nl.hva.election_backend.repository.UserRepository;
import nl.hva.election_backend.repository.VotingGuideAnswerRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VotingGuideAnswersService {
    private final VotingGuideAnswerRepository votingGuideAnswerRepository;
    private final UserRepository userRepository;
    private final StatementRepository statementRepository;

    public VotingGuideAnswersService(VotingGuideAnswerRepository votingGuideAnswerRepository,
                                     UserRepository userRepository,
                                     StatementRepository statementRepository) {
        this.votingGuideAnswerRepository = votingGuideAnswerRepository;
        this.userRepository = userRepository;
        this.statementRepository = statementRepository;
    }

    @Transactional
    public void saveAnswers(VotingGuideRequestDto votingGuideAnswers, Long userId) {
//        Check if user is valid
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

//        Check if user has answers in database
        boolean userHasAnswers = votingGuideAnswerRepository.existsByUserId(userId);
        if (userHasAnswers) votingGuideAnswerRepository.deleteAllByUserId(userId);

//        Convert set of answer dto's to list of answer entities
        List<VotingGuideAnswerEntity> listOfAnswerEntities = votingGuideAnswers.getVotingGuideAnswers()
                .stream()
                .map(answerDto -> {
                    StatementEntity statement = statementRepository.findById(answerDto.getStatementId())
                            .orElseThrow(() -> new RuntimeException("Statement not found"));
                    return new VotingGuideAnswerEntity(user, statement, answerDto.getAnswer());
                })
                .toList();
//        Save all answer entities
        votingGuideAnswerRepository.saveAll(listOfAnswerEntities);
    }

    public Set<VotingGuideAnswerDto> getAnswers(Long userId) {
        Set<VotingGuideAnswerEntity> entitySet = new HashSet<>(votingGuideAnswerRepository.findAllByUserId(userId));

//        Convert entity set to dto set
        return entitySet
                .stream()
                .map(entity -> new VotingGuideAnswerDto(
                        entity.getStatement().getId().intValue(),
                        entity.getAnswer()))
                .collect(Collectors.toSet());
    }
}
