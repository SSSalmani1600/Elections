package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.*;
import nl.hva.election_backend.exception.ForbiddenException;
import nl.hva.election_backend.service.DiscussionService;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscussionControllerTest {

    @Mock
    private DiscussionService discussionService;

    @Mock
    private ReactionService reactionService;

    private DiscussionController discussionController;

    @BeforeEach
    void setUp() {
        discussionController = new DiscussionController(discussionService, reactionService);
    }

    @Test
    @DisplayName("Lijst van discussies ophalen - succes")
    void list_ReturnsOk() {
        PageResponseDto<DiscussionListItemDto> pageResponse = new PageResponseDto<>(
                Collections.emptyList(), 0, 10, 0, 0, true
        );
        when(discussionService.list(anyInt(), anyInt())).thenReturn(pageResponse);

        ResponseEntity<PageResponseDto<DiscussionListItemDto>> response = discussionController.list(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
    }

    @Test
    @DisplayName("Nieuwe discussie aanmaken - succes")
    void create_Success() {
        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitle("Geldige Titel");
        request.setBody("Geldige inhoud voor de discussie");
        request.setCategory("politiek");
        request.setUserId(1L);
        
        DiscussionDetailDto detail = new DiscussionDetailDto("100", 1L, "Geldige Titel", "testuser", "Geldige inhoud", Instant.now(), Instant.now(), 0, Collections.emptyList());
        when(discussionService.createDiscussion(any())).thenReturn(detail);

        ResponseEntity<DiscussionDetailDto> response = discussionController.create(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("100", response.getBody().id());
    }

    @Test
    @DisplayName("Nieuwe discussie aanmaken - geblokkeerde inhoud (error flow)")
    void create_BlockedContent_ThrowsForbiddenException() {
        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setTitle("Slechte Titel");
        request.setBody("Slechte inhoud");
        request.setUserId(1L);
        
        when(discussionService.createDiscussion(any())).thenThrow(new ForbiddenException("Bericht bevat verboden inhoud."));

        assertThrows(ForbiddenException.class, () -> discussionController.create(request));
    }

    @Test
    @DisplayName("Reactie toevoegen - succes")
    void addReaction_Success() {
        CreateReactionRequest request = new CreateReactionRequest();
        request.setUserId(1L);
        request.setMessage("Leuke reactie");
        
        ReactionDto saved = new ReactionDto(500L, 1L, "testuser", "Leuke reactie", Instant.now());
        when(reactionService.addReaction(anyLong(), anyLong(), anyString())).thenReturn(saved);

        ResponseEntity<ReactionDto> response = discussionController.addReaction(1L, request);

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
}
