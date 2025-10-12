package nl.hva.election_backend.utils.xml.transformers;


import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.utils.xml.DefinitionTransformer;

import java.util.Map;

import static nl.hva.election_backend.utils.xml.TagAndAttributeNames.*;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchDefinitionTransformer implements DefinitionTransformer {
    private final Election election;

    /**
     * Creates a new transformer for handling the structure of the constituencies, municipalities and the parties.
     * It expects an instance of Election that can be used for storing the results.
     * @param election the election in which the votes wil be stored.
     */
    public DutchDefinitionTransformer(Election election) {
        this.election = election;
    }
    
    @Override
    public void registerRegion(Map<String, String> electionData) {
        System.out.println("Committee: " + electionData);
    }

    @Override
    public void registerParty(Map<String, String> electionData) {
        System.out.println("Party: " + electionData);
        String name = electionData.get(REGISTERED_APPELLATION);
        election.getParties().add(new Party(name));
    }
}
