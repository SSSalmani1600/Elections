package nl.hva.election_backend.dto;

import jakarta.validation.constraints.NotNull;

public class UserIdRequest {
    @NotNull(message = "userId is verplicht")
    private Long userId;

    public UserIdRequest() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}


