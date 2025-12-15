package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.MunicipalityDto;
import nl.hva.election_backend.service.MunicipalityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/electionresults")
public class MunicipalityController {
    private final MunicipalityService municipalityService;

    public MunicipalityController(MunicipalityService municipalityService) {
        this.municipalityService = municipalityService;
    }

    @GetMapping("/{election_id}/municipalities/{municipality_id}")
    public ResponseEntity<MunicipalityDto> getMunicipalityByName(
            @PathVariable("election_id") int electionId,
            @PathVariable("municipality_id") String name) {

        MunicipalityDto dto = municipalityService.getMunicipalityData(electionId, name);

        return ResponseEntity.ok(dto);
    }
}
