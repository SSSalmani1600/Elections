package nl.hva.election_backend.service;

import jakarta.transaction.Transactional;
import nl.hva.election_backend.dto.VotingGuideAnswerDto;
import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.entity.VotingGuideAnswerEntity;
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

    public VotingGuideAnswersService(VotingGuideAnswerRepository votingGuideAnswerRepository, UserRepository userRepository) {
        this.votingGuideAnswerRepository = votingGuideAnswerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveAnswers(VotingGuideRequestDto votingGuideAnswers, Long userId) {
//        Check if user is valid
        boolean userExists = userRepository.existsById(userId);
        if (!userExists) throw new RuntimeException("User not found");

//        Check if user has answers in database
        boolean userHasAnswers = votingGuideAnswerRepository.existsByUserId(userId);
        if (userHasAnswers) votingGuideAnswerRepository.deleteAllByUserId(userId);

//        Convert set of answer dto's to list of answer entities
        List<VotingGuideAnswerEntity> listOfAnswerEntities = votingGuideAnswers.getVotingGuideAnswers()
                .stream()
                .map(answerDto -> new VotingGuideAnswerEntity(
                        userId,
                        answerDto.getStatementId(),
                        answerDto.getAnswer()))
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
                        entity.getStatementId(),
                        entity.getAnswer()))
                .collect(Collectors.toSet());
    }
}
