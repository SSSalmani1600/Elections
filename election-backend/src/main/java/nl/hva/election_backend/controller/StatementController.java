package nl.hva.election_backend.controller;

import nl.hva.election_backend.entity.StatementEntity;
import nl.hva.election_backend.service.StatementService;
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
    public LinkedHashSet<StatementEntity> getAllStatements() {
        return this.statementService.getAllStatements();
    }
}
