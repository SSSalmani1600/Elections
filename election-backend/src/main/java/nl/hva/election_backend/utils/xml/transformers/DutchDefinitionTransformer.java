package nl.hva.election_backend.utils.xml.transformers;


import io.micrometer.common.util.StringUtils;
import nl.hva.election_backend.entity.MunicipalityEntity;
import nl.hva.election_backend.entity.PartyEntity;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.service.MunicipalityService;
import nl.hva.election_backend.service.PartyService;
import nl.hva.election_backend.utils.xml.DefinitionTransformer;
import nl.hva.election_backend.utils.xml.TagAndAttributeNames;

import java.util.Map;
import java.util.Objects;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchDefinitionTransformer implements DefinitionTransformer, TagAndAttributeNames {
    private final Election election;
    private final PartyService partyService;
    private final MunicipalityService municipalityService;
    private int currentPartyId = 1;

    /**
     * Creates a new transformer for handling the structure of the constituencies, municipalities and the parties.
     * It expects an instance of Election that can be used for storing the results.
     * @param election the election in which the votes wil be stored.
     */
    public DutchDefinitionTransformer(Election election, PartyService partyService, MunicipalityService municipalityService) {
        this.election = election;
        this.partyService = partyService;
        this.municipalityService = municipalityService;
    }

    @Override
    public void registerRegion(Map<String, String> electionData) {
        if (election.getType() == null && electionData.containsKey(ELECTION_NAME)) {
            election.setType(electionData.get(ELECTION_NAME));
            election.setDate(electionData.get(ELECTION_DATE));
            System.out.println("Election metadata loaded: " + election);
        }

        String category = electionData.get(REGION_CATEGORY_ATTRIBUTE);

        if (!Objects.equals(category, "GEMEENTE")) return;

        String year = electionData.get(ELECTION_DATE).split("-")[0];
        String contestId = electionData.get(SUPERIOR_REGION_NUMBER_ATTRIBUTE);
        String municipalityId = electionData.get(REGION_NUMBER_ATTRIBUTE);
        String name = electionData.get(REGION_NAME);

        MunicipalityEntity savedMunicipality = null;

        if (StringUtils.isNotBlank(year) &&
                StringUtils.isNotBlank(municipalityId) &&
                StringUtils.isNotBlank(contestId) &&
                StringUtils.isNotBlank(name)) {
            savedMunicipality = municipalityService
                    .saveIfNotExists(Integer.parseInt(year), municipalityId, contestId, name);
        }

        if (savedMunicipality == null) {
            System.out.println("municipality already exists in db");
            return;
        }

        System.out.println("Municipality saved: " + savedMunicipality.getYear() + " - " + savedMunicipality.getMunicipalityId());
    }
    @Override
    public void registerParty(Map<String, String> electionData) {
        String name = electionData.get(REGISTERED_APPELLATION);
        String electionYear = electionData.get(ELECTION_DATE);

        String partyId = String.valueOf(currentPartyId);
        int year = Integer.parseInt(electionYear.substring(0, 4));
        PartyEntity saveParty = partyService.saveIfNotExists(name, year, partyId);
        currentPartyId++;


        System.out.println("Party saved: " + saveParty.getYear() + " - " + saveParty.getPartyId());
    }

}
