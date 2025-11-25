package nl.hva.election_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "statements")
public class StatementEntity {
    @Id
    private Long id;
    private String statement;
    private String category;
    private String explanation;

    public StatementEntity() {}

    public StatementEntity(Long id, String statement, String category, String explanation) {
        this.id = id;
        this.statement = statement;
        this.category = category;
        this.explanation = explanation;
    }

    public Long getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public String getCategory() {
        return category;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
