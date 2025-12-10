package nl.hva.election_backend.dto;

public class VotingGuideAnswerDto {
    private Long statementId;
    private String answer;

    public VotingGuideAnswerDto(Long statementId, String answer) {
        this.statementId = statementId;
        this.answer = answer;
    }

    public Long getStatementId() {
        return statementId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setStatementId(Long statementId) {
        this.statementId = statementId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
