package nl.hva.election_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

<<<<<<< HEAD
public class CreateDiscussionRequest {

    @NotBlank(message = "Titel is verplicht")
    @Size(min = 5, max = 255, message = "Titel moet tussen de 5 en 255 karakters zijn")
    private String title;

    @NotBlank(message = "Inhoud is verplicht")
    @Size(min = 10, message = "Inhoud moet minimaal 10 karakters bevatten")
    private String body;

    private String category;

    @NotNull(message = "userId is verplicht")
    private Long userId;

    public CreateDiscussionRequest() {}

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
=======
public record CreateDiscussionRequest(
        @NotBlank(message = "Titel is verplicht")
        @Size(min = 5, max = 255, message = "Titel moet tussen de 5 en 255 karakters zijn")
        String title,

        @NotBlank(message = "Inhoud is verplicht")
        @Size(min = 10, message = "Inhoud moet minimaal 10 karakters bevatten")
        String body,

        String category,

        @NotNull(message = "userId is verplicht")
        Long userId
) {
}

>>>>>>> 0896198cbe36a71fcdb10e5205bd00ac344da846
