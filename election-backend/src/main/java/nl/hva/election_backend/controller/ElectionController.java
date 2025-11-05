package nl.hva.election_backend.controller;

import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class ElectionController {

    private final ElectionService electionService;


    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }


    @GetMapping("/api/next-elections")
    public ResponseEntity<List<Election>> getNextElections() throws IOException {
        List<Election> elections = electionService.fetchUpcomingElections();
        return ResponseEntity.ok(elections);
    }
}
