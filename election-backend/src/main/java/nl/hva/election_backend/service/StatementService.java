package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.StatementEntity;
import nl.hva.election_backend.repository.StatementRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;

@Service
public class StatementService {
    private final StatementRepository statementRepository;

    public StatementService(StatementRepository statementRepository) {
        this.statementRepository = statementRepository;
    }

    public LinkedHashSet<StatementEntity> getAllStatements() {
        return this.statementRepository.findAllByOrderByIdAsc();
    }
}
