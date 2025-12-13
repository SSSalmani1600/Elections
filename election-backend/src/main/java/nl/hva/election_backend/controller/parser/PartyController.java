package nl.hva.election_backend.controller.parser;

import nl.hva.election_backend.dto.PartyDetail;
import nl.hva.election_backend.service.PartyService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import nl.hva.election_backend.entity.PartyEntity;

@RestController
@RequestMapping("api/parties")
public class PartyController {

    private final PartyService partyService;

    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    @GetMapping
    public Set<PartyEntity> getAllParties() {
        return partyService.getParties();
    }

    @GetMapping("/{partyId}")
    public PartyDetail getDetail(@PathVariable String partyId) {
        return partyService.getPartyDetail(partyId);
    }

}
