package nl.hva.election_backend.dto;

public class VotingGuideAnswerDto {
    private Number statementId;
    private String answer;

    public VotingGuideAnswerDto(Number statementId, String answer) {
        this.statementId = statementId;
        this.answer = answer;
    }

    public Number getStatementId() {
        return statementId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setStatementId(Number statementId) {
        this.statementId = statementId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
