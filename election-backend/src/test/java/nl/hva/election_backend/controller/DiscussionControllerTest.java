package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.*;
import nl.hva.election_backend.exception.ForbiddenException;
import nl.hva.election_backend.service.DiscussionService;
import nl.hva.election_backend.service.ModerationService;
import nl.hva.election_backend.service.ReactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscussionControllerTest {

    @Mock
    private DiscussionService discussionService;

    @Mock
    private ReactionService reactionService;

    @Mock
    private ModerationService moderationService;

    private DiscussionController discussionController;

    @BeforeEach
    void setUp() {
        discussionController = new DiscussionController(discussionService, reactionService, moderationService);
    }

    @Test
    @DisplayName("Lijst van discussies ophalen")
    void list_ReturnsOk() {
        when(discussionService.list(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        ResponseEntity<List<DiscussionListItemDto>> response = discussionController.list(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Nieuwe discussie aanmaken - succes")
    void create_Success() {
<<<<<<< HEAD
        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitle("Geldige Titel");
        request.setBody("Geldige inhoud voor de discussie");
        request.setCategory("politiek");
        request.setUserId(1L);
        
        when(moderationService.moderateText(anyString())).thenAnswer(i -> {
            ModerationResult res = new ModerationResult((String) i.getArgument(0));
            res.setModerationStatus("PENDING");
            return res;
        });
=======
        CreateDiscussionRequest request = new CreateDiscussionRequest("Geldige Titel", "Geldige inhoud voor de discussie", "politiek", 1L);
        
        when(moderationService.moderateText(anyString())).thenAnswer(i -> new ModerationResult(i.getArgument(0), "PENDING", Collections.emptyList()));
>>>>>>> 0896198cbe36a71fcdb10e5205bd00ac344da846
        when(discussionService.createDiscussion(anyString(), anyString(), anyString(), anyLong())).thenReturn(100L);
        
        DiscussionDetailDto detail = new DiscussionDetailDto("100", 1L, "Geldige Titel", "testuser", "Geldige inhoud", Instant.now(), Instant.now(), 0, Collections.emptyList());
        when(discussionService.getDetailById(100L)).thenReturn(detail);

        ResponseEntity<DiscussionDetailDto> response = discussionController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("100", response.getBody().id());
    }

    @Test
    @DisplayName("Nieuwe discussie aanmaken - geblokkeerde inhoud")
    void create_BlockedContent_ThrowsForbiddenException() {
<<<<<<< HEAD
        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitle("Slechte Titel");
        request.setBody("Slechte inhoud");
        request.setCategory("politiek");
        request.setUserId(1L);
        
        ModerationResult blockedResult = new ModerationResult("Slechte Titel");
        blockedResult.setModerationStatus("BLOCKED");
        blockedResult.setBlocked(true);
        when(moderationService.moderateText(anyString())).thenReturn(blockedResult);

        assertThrows(ForbiddenException.class, () -> discussionController.create(request));
    }

    @Test
    @DisplayName("Reactie toevoegen - succes")
    void addReaction_Success() {
        CreateReactionRequest request = new CreateReactionRequest();
        request.setUserId(1L);
        request.setMessage("Leuke reactie");
        
        when(moderationService.moderateText(anyString())).thenAnswer(i -> {
            ModerationResult res = new ModerationResult((String) i.getArgument(0));
            res.setModerationStatus("APPROVED");
            return res;
        });
        
        ReactionEntity saved = new ReactionEntity();
        saved.setId(500L);
        when(reactionService.addReaction(anyLong(), anyLong(), anyString())).thenReturn(saved);

        ResponseEntity<ReactionEntity> response = discussionController.addReaction(1L, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(500L, response.getBody().getId());
    }

    @Test
    @DisplayName("Reactie verwijderen - succes")
    void deleteReaction_Success() {
        ResponseEntity<Map<String, String>> response = discussionController.deleteReaction(500L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reactie verwijderd", response.getBody().get("message"));
    }
=======
        CreateDiscussionRequest request = new CreateDiscussionRequest("Slechte Titel", "Slechte inhoud", "politiek", 1L);
        
        when(moderationService.moderateText(anyString())).thenReturn(new ModerationResult("Slechte Titel", "BLOCKED", List.of("ongepast")));

        assertThrows(ForbiddenException.class, () -> discussionController.create(request));
    }
>>>>>>> 0896198cbe36a71fcdb10e5205bd00ac344da846
}

