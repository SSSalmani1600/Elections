package nl.hva.election_backend.controller.parser;

import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.service.DutchElectionService;
import org.springframework.web.bind.annotation.*;

/**
 * Demo controller for showing how you could load the election data in the backend.
 */
@RestController
@RequestMapping("api/elections")
public class ParserElectionController {
    private final DutchElectionService electionService;

    public ParserElectionController(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    /**
     * Processes the result for a specific election.
     *
     * @param electionId the id of the election, e.g. the value of the Id attribute from the ElectionIdentifier tag.
     * @param folderName the name of the folder that contains the XML result files. If none is provided the value from
     *                   the electionId is used.
     * @return Election if the results have been processed successfully. Please be sure yoy don't output all the data!
     * Just the general data about the election should be sent back to the front-end!<br/>
     * <i>If you want to return something else please feel free to do so!</i>
     */
    @PostMapping("{electionId}")
    public Election readResults(@PathVariable String electionId, @RequestParam(required = false) String folderName) {
        if (folderName == null) {
            return electionService.readResults(electionId, electionId);
        } else {
            return electionService.readResults(electionId, folderName);
        }
    }
}
