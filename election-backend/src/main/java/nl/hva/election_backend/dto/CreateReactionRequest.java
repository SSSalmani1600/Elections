package nl.hva.election_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReactionRequest {
    @NotNull(message = "userId is verplicht")
    private Long userId;

    @NotBlank(message = "Bericht mag niet leeg zijn")
    @Size(min = 2, message = "Bericht moet minimaal 2 karakters bevatten")
    private String message;

    public CreateReactionRequest() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
