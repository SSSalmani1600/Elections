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
        CreateDiscussionRequest request = new CreateDiscussionRequest("Geldige Titel", "Geldige inhoud voor de discussie", "politiek", 1L);
        
        when(moderationService.moderateText(anyString())).thenAnswer(i -> new ModerationResult(i.getArgument(0), "PENDING", Collections.emptyList()));
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
        CreateDiscussionRequest request = new CreateDiscussionRequest("Slechte Titel", "Slechte inhoud", "politiek", 1L);
        
        when(moderationService.moderateText(anyString())).thenReturn(new ModerationResult("Slechte Titel", "BLOCKED", List.of("ongepast")));

        assertThrows(ForbiddenException.class, () -> discussionController.create(request));
    }
}

