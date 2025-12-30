package nl.hva.election_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

