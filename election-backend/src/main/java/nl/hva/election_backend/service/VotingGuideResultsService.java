package nl.hva.election_backend.service;

import jakarta.transaction.Transactional;
import nl.hva.election_backend.dto.VotingGuideAnswerDto;
import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.dto.VotingGuideResponseDto;
import nl.hva.election_backend.dto.VotingGuideResultDto;
import nl.hva.election_backend.entity.PartyViewpointEntity;
import nl.hva.election_backend.entity.VotingGuideAnswerEntity;
import nl.hva.election_backend.entity.VotingGuidePartyEntity;
import nl.hva.election_backend.entity.VotingGuideResultEntity;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.UserRepository;
import nl.hva.election_backend.repository.VotingGuidePartyRepository;
import nl.hva.election_backend.repository.VotingGuideResultsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VotingGuideResultsService {
    private final VotingGuidePartyRepository votingGuidePartyRepository;
    private final UserRepository userRepository;
    private final VotingGuideResultsRepository votingGuideResultsRepository;

    public VotingGuideResultsService(VotingGuidePartyRepository votingGuidePartyRepository, UserRepository userRepository, VotingGuideResultsRepository votingGuideResultsRepository) {
        this.votingGuidePartyRepository = votingGuidePartyRepository;
        this.userRepository = userRepository;
        this.votingGuideResultsRepository = votingGuideResultsRepository;
    }

//    Compares answers of the user to the viewpoints per party, and gives back a dto with a List of parties with the corresponding percentage
    public VotingGuideResponseDto calculate(VotingGuideRequestDto votingGuideAnswers, List<PartyViewpointEntity> partyViewpoints) {
        if (votingGuideAnswers == null ||
                votingGuideAnswers.getVotingGuideAnswers() == null ||
                votingGuideAnswers.getVotingGuideAnswers().isEmpty()) {
            throw new IllegalArgumentException("Voting guide answers are required");
        }

        if (partyViewpoints == null || partyViewpoints.isEmpty()) {
            throw new IllegalArgumentException("Party viewpoints are required");
        }

        Set<VotingGuideResultDto> sortedSet = new TreeSet<>(Comparator.comparing(VotingGuideResultDto::getPercentage).reversed().thenComparing(VotingGuideResultDto::getPartyId));
        double totalAnswers = votingGuideAnswers.getVotingGuideAnswers().size();
        Map<Long, Map<Long, String>> partyViewpointsMap = new HashMap<>();

//        Converting List to nested Map
        partyViewpoints.forEach(partyViewpoint -> {
            Long partyId = partyViewpoint.getPartyId();
            Long statementId = partyViewpoint.getStatementId();
            if (!partyViewpointsMap.containsKey(partyId)) {
                partyViewpointsMap.put(partyId, new HashMap<>());
                Map<Long, String> innerMap = partyViewpointsMap.get(partyId);
                innerMap.put(statementId, partyViewpoint.getPosition());
            } else {
                Map<Long, String> innerMap = partyViewpointsMap.get(partyId);
                innerMap.put(statementId, partyViewpoint.getPosition());
            }
        });

//        Looping through parties in map, comparing statements and checking if user answer is equals to party position
        for (Map.Entry<Long, Map<Long, String>> entry : partyViewpointsMap.entrySet()) {
            double score = 0;

            for (VotingGuideAnswerDto votingGuideAnswer : votingGuideAnswers.getVotingGuideAnswers()) {
                Long userStatementId = votingGuideAnswer.getStatementId().longValue();
                String userPosition = votingGuideAnswer.getAnswer();

                Map<Long, String> innerMap = entry.getValue();
                if (innerMap.containsKey(userStatementId)) {
                    if (innerMap.get(userStatementId).equals(userPosition)) {
                        score++;
                    }
                }
            }
            double percentage = (score / totalAnswers) * 100;

            Optional<VotingGuidePartyEntity> party = votingGuidePartyRepository.findById(entry.getKey());
            String partyName = party.isPresent() ? party.get().getName() : "Onbekende partij";
            sortedSet.add(new VotingGuideResultDto(entry.getKey(), partyName, percentage));
        }

        List<VotingGuideResultDto> sortedList = new ArrayList<>(sortedSet);

        return new VotingGuideResponseDto(sortedList);
    }

    @Transactional
    public void saveResults(VotingGuideResponseDto votingGuideResults, Long userId) {
//        Check if user is valid
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

//        Check if user has results in database
        boolean userHasResults = votingGuideResultsRepository.existsByUserId(userId);
        if (userHasResults) votingGuideResultsRepository.deleteAllByUserId(userId);

//        Convert set of result dto's to list of result entities
        List<VotingGuideResultEntity> listOfResultEntities = votingGuideResults.getVotingGuideResults()
                .stream()
                .map(resultDto -> {
                    VotingGuidePartyEntity party = votingGuidePartyRepository.findById(resultDto.getPartyId())
                            .orElseThrow(() -> new RuntimeException("Party not found"));
                    return new VotingGuideResultEntity(
                        user,
                        party,
                        resultDto.getPartyName(),
                        (long) resultDto.getPercentage());
                })
                .toList();
//        Save all result entities
        votingGuideResultsRepository.saveAll(listOfResultEntities);
    }

    public VotingGuideResponseDto getResults(Long userId) {
        List<VotingGuideResultEntity> entitySet = new ArrayList<>(votingGuideResultsRepository.findAllByUserIdOrderByPercentageDescPartyIdAsc(userId));

//        Convert entity set to dto set
        return new VotingGuideResponseDto(entitySet
                .stream()
                .map(entity -> new VotingGuideResultDto(
                        entity.getParty().getId(),
                        entity.getPartyName(),
                        entity.getPercentage()))
                .collect(Collectors.toList()));
    }

    public boolean userHasResults(Long userId) {
        return votingGuideResultsRepository.existsByUserId(userId);
    }
}
