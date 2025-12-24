package nl.hva.election_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateDiscussionRequest(

        @NotBlank
        String userId,
        @NotBlank
        @Min(6)
        @Max(value=50, message = "Veels te l;amg...")
        String title,
        String body
) {
}
