package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.VotingGuideAnswerDto;
import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.entity.VotingGuideAnswerEntity;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.UserRepository;
import nl.hva.election_backend.repository.VotingGuideAnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VotingGuideAnswersService {
    private final VotingGuideAnswerRepository votingGuideAnswerRepository;
    private final UserRepository userRepository;

    public VotingGuideAnswersService(VotingGuideAnswerRepository votingGuideAnswerRepository, UserRepository userRepository) {
        this.votingGuideAnswerRepository = votingGuideAnswerRepository;
        this.userRepository = userRepository;
    }

    public void saveAnswers(VotingGuideRequestDto votingGuideAnswers, User user) {
//        Check if user is valid
        boolean userExists = userRepository.existsById(user.getId());
        if (!userExists) throw new RuntimeException("User not found");

//        Check if user has answers in database
        boolean userHasAnswers = votingGuideAnswerRepository.existsByUserId(user.getId());
        if (userHasAnswers) votingGuideAnswerRepository.deleteAllByUserId(user.getId());

//        Convert set of answer dto's to list of answer entities
        List<VotingGuideAnswerEntity> listOfAnswerEntities = votingGuideAnswers.getVotingGuideAnswers()
                .stream()
                .map(answerDto -> new VotingGuideAnswerEntity(
                        user.getId(),
                        answerDto.getStatementId(),
                        answerDto.getAnswer()))
                .toList();
//        Save all answer entities
        votingGuideAnswerRepository.saveAll(listOfAnswerEntities);
    }
}
