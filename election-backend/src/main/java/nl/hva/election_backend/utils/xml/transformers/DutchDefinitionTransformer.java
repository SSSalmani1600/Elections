package nl.hva.election_backend.utils.xml.transformers;


import nl.hva.election_backend.entity.PartyEntity;
import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.service.PartyService;
import nl.hva.election_backend.utils.xml.DefinitionTransformer;

import java.util.Map;

import static nl.hva.election_backend.utils.xml.TagAndAttributeNames.*;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchDefinitionTransformer implements DefinitionTransformer {
    private final Election election;
    private final PartyService partyService;

    /**
     * Creates a new transformer for handling the structure of the constituencies, municipalities and the parties.
     * It expects an instance of Election that can be used for storing the results.
     * @param election the election in which the votes wil be stored.
     */
    public DutchDefinitionTransformer(Election election, PartyService partyService) {
        this.election = election;
        this.partyService = partyService;
    }

    @Override
    public void registerRegion(Map<String, String> electionData) {
        if (election.getType() == null && electionData.containsKey(ELECTION_NAME)) {
            election.setType(electionData.get(ELECTION_NAME));
            election.setDate(electionData.get(ELECTION_DATE));
            System.out.println("Election metadata loaded: " + election);
        }

        System.out.println("Committee/Region: " + electionData);
    }
    @Override
    public void registerParty(Map<String, String> electionData) {
        String name = electionData.get(REGISTERED_APPELLATION);

        int year = Integer.parseInt(election.getDate().substring(0, 4));
        PartyEntity saveParty = partyService.saveIfNotExists(name, year);
        System.out.println("Party saved: " + saveParty.getYear() + " - " + saveParty.getId());
    }

}
