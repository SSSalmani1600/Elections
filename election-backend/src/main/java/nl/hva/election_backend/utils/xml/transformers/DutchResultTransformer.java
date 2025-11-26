package nl.hva.election_backend.utils.xml.transformers;

import nl.hva.election_backend.model.Candidate;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.utils.xml.TagAndAttributeNames;
import nl.hva.election_backend.utils.xml.VotesTransformer;

import java.util.Map;
import java.util.Objects;

/**
 * Just prints the content of electionData to the standard output.<br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchResultTransformer implements VotesTransformer, TagAndAttributeNames {
    private final Election election;

    /**
     * Creates a new transformer for handling the votes at the results. It expects an instance of
     * Election that can be used for storing the results. The results contain either which party has candidates who
     * have been elected or the candidates who are elected.
     *
     * @param election the election in which the votes will be stored.
     */
    public DutchResultTransformer(Election election) { this.election = election; }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {

        System.out.printf("National party result: %s%n", electionData);


        String partyName = electionData.get(REGISTERED_NAME);
        String NUMBER_OF_VOTES = "NumberOfVotes";
        String votesStr = electionData.get(NUMBER_OF_VOTES);

        if (partyName == null || votesStr == null) {
            System.err.println("⚠️ Missing data for party result: " + electionData);
            return;
        }

        try {
            int votes = Integer.parseInt(votesStr);
            election.getParties().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(partyName))
                    .findFirst()
                    .ifPresentOrElse(
                            p -> {
                                p.setVotes(votes);
                                System.out.printf("✅ Stored %d votes for party %s%n", votes, partyName);
                            },
                            () -> System.err.printf("⚠️ Party not found in election: %s%n", partyName)
                    );
        } catch (NumberFormatException e) {
            System.err.printf("⚠️ Invalid vote number '%s' for party %s%n", votesStr, partyName);
        }
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {

        System.out.printf("National candidate result: %s%n", electionData);


        String namePrefix = electionData.get(NAME_PREFIX);
        String firstName = electionData.get(FIRST_NAME);
        String lastName = electionData.get(LAST_NAME);
        String fullName = (namePrefix == null || namePrefix.isBlank())
                ? firstName + " " + lastName
                : firstName + " " + namePrefix + " " + lastName;
        String isElected = electionData.get(ELECTED);
        String shortCode = electionData.get(CANDIDATE_IDENTIFIER + "-" + SHORT_CODE);
        boolean isBoolean = Objects.equals(isElected, "yes");

        Candidate candidate = election.getCandidateByName(fullName);
        if (candidate != null) {
            candidate.setElected(isBoolean);
            candidate.setShortCode(shortCode);
        } else {
            System.err.printf("Candidate not found for fullName='%s'%n", fullName);
        }

    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        // intentional exception behouden
        throw new IllegalStateException("There is no implementation on purpose.");
    }
}
