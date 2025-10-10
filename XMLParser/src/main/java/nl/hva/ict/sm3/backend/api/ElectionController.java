package nl.hva.ict.sm3.backend.api;

import nl.hva.ict.sm3.backend.model.Affiliation;
import nl.hva.ict.sm3.backend.model.Election;
import nl.hva.ict.sm3.backend.service.DutchElectionService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Demo controller for showing how you could load the election data in the backend.
 */
@RestController
@RequestMapping("elections")
public class ElectionController {
    private final DutchElectionService electionService;

    public ElectionController(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    /**
     * Processes the result for a specific election.
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

    @PostMapping("{electionId}/affiliations")
    public Set<String> readAffiliations(@PathVariable String electionId, @RequestParam(required = false) String folderName) {
        Election result;
        if (folderName == null) {
            result = electionService.readResults(electionId, electionId);
        } else {
            result = electionService.readResults(electionId, folderName);
        }
        return result.getAffiliations().stream().map(Affiliation::getName).collect(Collectors.toSet());
    }
}
