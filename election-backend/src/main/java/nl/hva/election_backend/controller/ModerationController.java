package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.TextRequest;
import nl.hva.election_backend.dto.ModerationResult;
import nl.hva.election_backend.service.ModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @PostMapping("/moderateText")
    public ResponseEntity<ModerationResult> moderateText(@RequestBody TextRequest request) {

        ModerationResult result = moderationService.moderateText(request.getText());

        if (result.isBlocked()) {
            return ResponseEntity.status(403).body(result);
        }

        return ResponseEntity.ok(result);
    }

}
