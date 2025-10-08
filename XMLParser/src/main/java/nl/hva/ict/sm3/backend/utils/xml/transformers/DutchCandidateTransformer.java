package nl.hva.ict.sm3.backend.utils.xml.transformers;

import nl.hva.ict.sm3.backend.model.Candidate;
import nl.hva.ict.sm3.backend.model.Election;
import nl.hva.ict.sm3.backend.utils.xml.CandidateTransformer;
import nl.hva.ict.sm3.backend.utils.xml.TagAndAttributeNames;

import java.util.Map;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchCandidateTransformer implements CandidateTransformer, TagAndAttributeNames {
    private final Election election;

    /**
     * Creates a new transformer for handling the candidate lists. It expects an instance of Election that can
     * be used for storing the candidates lists.
     * @param election the election in which the candidate lists wil be stored.
     */
    public DutchCandidateTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerCandidate(Map<String, String> electionData) {
        String firstName = electionData.get(FIRST_NAME);
        String lastName = electionData.get(LAST_NAME);
        String initials = electionData.get(NAME_LINE);
        String gender = electionData.get(GENDER);
        String localityName = electionData.get(LOCALITY_NAME);
        String electionId = electionData.get(ELECTION_IDENTIFIER);
        String electionName = electionData.get(ELECTION_NAME);
        String affiliationId = (electionData.get(AFFILIATION_IDENTIFIER));
        Candidate candidate = new Candidate(firstName, lastName, initials, gender, localityName, electionId, electionName, affiliationId);
        election.getCandidates().add(candidate);
    }
}
