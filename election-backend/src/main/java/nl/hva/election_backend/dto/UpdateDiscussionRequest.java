package nl.hva.election_backend.dto;

import jakarta.validation.constraints.NotBlank;
<<<<<<< HEAD
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateDiscussionRequest {

    @NotNull(message = "userId is verplicht")
    private Long userId;

    @NotBlank(message = "Titel is verplicht")
    @Size(min = 5, max = 255, message = "Titel moet tussen de 5 en 255 karakters zijn")
    private String title;

    @NotBlank(message = "Inhoud is verplicht")
    @Size(min = 10, message = "Inhoud moet minimaal 10 karakters bevatten")
    private String body;

    public UpdateDiscussionRequest() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
=======
import jakarta.validation.constraints.Size;

public record UpdateDiscussionRequest(

        @NotBlank
        String userId,

        @NotBlank(message = "Titel is verplicht")
        @Size(min = 5, max = 255, message = "Titel moet tussen de 5 en 255 karakters zijn")
        String title,

        @NotBlank(message = "Inhoud is verplicht")
        @Size(min = 10, message = "Inhoud moet minimaal 10 karakters bevatten")
        String body
) {
>>>>>>> 0896198cbe36a71fcdb10e5205bd00ac344da846
}
