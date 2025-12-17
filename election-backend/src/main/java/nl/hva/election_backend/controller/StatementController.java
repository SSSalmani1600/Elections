package nl.hva.election_backend.controller;

import nl.hva.election_backend.entity.StatementEntity;
import nl.hva.election_backend.service.StatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;

@RestController
@RequestMapping("/api/statements")
public class StatementController {
    private final StatementService statementService;

    public StatementController(StatementService statementService) {
        this.statementService = statementService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllStatements() {
        try {
            return ResponseEntity.ok(statementService.getAllStatements());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Something went wrong with fetching all statements");
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            return ResponseEntity.ok(statementService.getAllCategories());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Something went wrong with fetching categories");
        }
    }

}
