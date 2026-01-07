package nl.hva.election_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePollRequest(

        @NotBlank(message = "Vraag mag niet leeg zijn")
        @Size(min = 10, message = "Vraag moet minimaal 10 tekens bevatten")
        String question

) {}
