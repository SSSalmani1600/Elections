package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.ElectionEntity;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.repository.ElectionRepository;
import nl.hva.election_backend.utils.xml.DutchElectionParser;
import nl.hva.election_backend.utils.xml.transformers.*;
import nl.hva.election_backend.utils.PathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.time.LocalDate;

/**
 * A demo service for demonstrating how an EML-XML parser can be used inside a backend application.<br/>
 * <br/>
 * <i><b>NOTE: </b>There are some TODO's and FIXME's present that need fixing!</i>
 */
@Service
public class DutchElectionService {
    private Election election;
    private final PartyService partyService;
    private final ElectionRepository electionRepository;

    public DutchElectionService(PartyService partyService, ElectionRepository electionRepository) {
        this.partyService = partyService;
        this.electionRepository = electionRepository;
    }

    public Election readResults(String electionId, String folderName) {
        System.out.println("Processing files...");

        this.election = new Election(electionId);
        System.out.println(this.election);

        int year = Integer.parseInt(electionId.replaceAll("\\D", ""));


        System.out.println("Election year - " + year);
        ElectionEntity electionEntity = electionRepository.findById(year)
                .orElseGet(() -> electionRepository.save(
                        new ElectionEntity(year, electionId, "Tweede Kamerverkiezing", null)
                ));
        System.out.println("Dutch Election results: " + election);

        // TODO This lengthy construction of the parser should be replaced with a fitting design pattern!
        //  And refactoring the constructor while your at it is also a good idea.
        DutchElectionParser electionParser = new DutchElectionParser(
                new DutchDefinitionTransformer(election, partyService),
                new DutchCandidateTransformer(election),
                new DutchResultTransformer(election),
                new DutchNationalVotesTransformer(election),
                new DutchConstituencyVotesTransformer(election),
                new DutchMunicipalityVotesTransformer(election)
        );

        try {
            // Assuming the election data is somewhere on the class-path it should be found.
            // Please note that you can also specify an absolute path to the folder!
            electionParser.parseResults(electionId, PathUtils.getResourcePath("/%s".formatted(folderName)));
            // Do what ever you like to do
            electionEntity.setDate(LocalDate.parse(election.getDate()));
            electionRepository.save(electionEntity);
            // Now is also the time to send the election information to a database for example.

            this.election = election;
            return this.election;
        } catch (IOException | XMLStreamException | NullPointerException | ParserConfigurationException |
                 SAXException e) {
            // FIXME You should do here some proper error handling! The code below is NOT how you handle errors properly!
            System.err.println("Failed to process the election results!");
            e.printStackTrace();
            return null;
        }
    }

    public Election getElection() {
        return this.election;
    }
}
