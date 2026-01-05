package nl.hva.election_backend.service;

// Test voor het bewerken van een discussie/topic

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.exception.ResourceNotFoundException;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.repository.UserRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito aanzetten
class DiscussionServiceTest {

    @Mock // nep repository
    private DiscussionRepository discussionRepository;

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private UserRepository userRepository;

    private DiscussionService discussionService;

    private DiscussionEntity testDiscussion;
    private User testUser;
    private final Long DISCUSSION_ID = 1L;
    private final Long OWNER_USER_ID = 100L;
    private final Long OTHER_USER_ID = 999L;

    @BeforeEach // draait voor elke test
    void setUp() {
        discussionService = new DiscussionService(
                discussionRepository,
                reactionRepository,
                userRepository
        );

        testUser = new User();
        testUser.setId(OWNER_USER_ID);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.nl");

        testDiscussion = new DiscussionEntity();
        testDiscussion.setId(DISCUSSION_ID);
        testDiscussion.setTitle("Originele Titel");
        testDiscussion.setBody("Originele inhoud");
        testDiscussion.setUser(testUser);
        testDiscussion.setCreatedAt(Instant.now());
        testDiscussion.setLastActivityAt(Instant.now());
        testDiscussion.setReactionsCount(0);
    }

    // Test: eigenaar kan eigen topic bewerken
    @Test
    @DisplayName("Eigenaar kan eigen topic bewerken - Happy Path")
    void updateDiscussion_AsOwner_Success() {
        when(discussionRepository.findById(DISCUSSION_ID))
                .thenReturn(Optional.of(testDiscussion));
        when(discussionRepository.save(any(DiscussionEntity.class)))
                .thenReturn(testDiscussion);
        when(userRepository.findById(OWNER_USER_ID))
                .thenReturn(Optional.of(testUser));
        when(reactionRepository.findAllByDiscussion_IdOrderByCreatedAtAsc(DISCUSSION_ID))
                .thenReturn(Collections.emptyList());

        DiscussionDetailDto result = discussionService.updateDiscussion(
                DISCUSSION_ID,
                OWNER_USER_ID,
                "Nieuwe Titel",
                "Nieuwe Body"
        );

        assertNotNull(result);
        verify(discussionRepository, times(1)).save(any(DiscussionEntity.class));
    }

    // Test: iemand anders mag niet bewerken
    @Test
    @DisplayName("Niet-eigenaar kan geen topic bewerken - Security Check")
    void updateDiscussion_AsNonOwner_ThrowsSecurityException() {
        when(discussionRepository.findById(DISCUSSION_ID))
                .thenReturn(Optional.of(testDiscussion));

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> discussionService.updateDiscussion(
                        DISCUSSION_ID,
                        OTHER_USER_ID, // niet de eigenaar
                        "Nieuwe Titel",
                        "Nieuwe Body"
                )
        );

        assertEquals("Je kunt alleen je eigen discussies bewerken", exception.getMessage());
        verify(discussionRepository, never()).save(any(DiscussionEntity.class));
    }

    // Test: topic bestaat niet
    @Test
    @DisplayName("Topic niet gevonden - 404 scenario")
    void updateDiscussion_NotFound_ThrowsResourceNotFoundException() {
        Long nonExistentId = 999L;
        when(discussionRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> discussionService.updateDiscussion(
                        nonExistentId,
                        OWNER_USER_ID,
                        "Nieuwe Titel",
                        "Nieuwe Body"
                )
        );

        assertEquals("Discussie niet gevonden", exception.getMessage());
        verify(discussionRepository, never()).save(any(DiscussionEntity.class));
    }

    // Test: timestamp wordt geupdate
    @Test
    @DisplayName("LastActivityAt wordt bijgewerkt bij bewerken")
    void updateDiscussion_UpdatesLastActivityAt() {
        Instant oldTime = Instant.parse("2024-01-01T00:00:00Z");
        testDiscussion.setLastActivityAt(oldTime);

        when(discussionRepository.findById(DISCUSSION_ID))
                .thenReturn(Optional.of(testDiscussion));
        when(discussionRepository.save(any(DiscussionEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(OWNER_USER_ID))
                .thenReturn(Optional.of(testUser));
        when(reactionRepository.findAllByDiscussion_IdOrderByCreatedAtAsc(DISCUSSION_ID))
                .thenReturn(Collections.emptyList());

        discussionService.updateDiscussion(
                DISCUSSION_ID,
                OWNER_USER_ID,
                "Nieuwe Titel",
                "Nieuwe Body"
        );

        assertTrue(testDiscussion.getLastActivityAt().isAfter(oldTime));
    }

    // Test: Discussie aanmaken
    @Test
    @DisplayName("Nieuwe discussie aanmaken - Happy Path")
    void createDiscussion_Success() {
        when(userRepository.findById(OWNER_USER_ID)).thenReturn(Optional.of(testUser));
        when(discussionRepository.save(any(DiscussionEntity.class))).thenAnswer(invocation -> {
            DiscussionEntity saved = invocation.getArgument(0);
            saved.setId(123L);
            return saved;
        });

        Long newId = discussionService.createDiscussion("Nieuwe Titel", "Nieuwe inhoud", "politiek", OWNER_USER_ID);

        assertEquals(123L, newId);
        verify(discussionRepository, times(1)).save(any(DiscussionEntity.class));
    }

    // Test: Reactie toevoegen
    @Test
    @DisplayName("Reactie toevoegen - Happy Path")
    void addReaction_Success() {
        when(discussionRepository.findById(DISCUSSION_ID)).thenReturn(Optional.of(testDiscussion));
        when(userRepository.findById(OWNER_USER_ID)).thenReturn(Optional.of(testUser));
        when(reactionRepository.save(any(nl.hva.election_backend.entity.ReactionEntity.class))).thenAnswer(i -> {
            nl.hva.election_backend.entity.ReactionEntity r = i.getArgument(0);
            r.setId(500L);
            return r;
        });

        nl.hva.election_backend.dto.ReactionDto result = discussionService.addReaction(DISCUSSION_ID, OWNER_USER_ID, "Dit is een nette reactie");

        assertNotNull(result);
<<<<<<< HEAD
        assertEquals(500L, result.getId());
=======
        assertEquals(500L, result.id());
>>>>>>> 0896198cbe36a71fcdb10e5205bd00ac344da846
        assertEquals(1, testDiscussion.getReactionsCount());
    }

    // Test: Reactie met scheldwoord
    @Test
    @DisplayName("Reactie met scheldwoord wordt geblokkeerd")
    void addReaction_BannedWord_ThrowsException() {
        when(discussionRepository.findById(DISCUSSION_ID)).thenReturn(Optional.of(testDiscussion));
        when(userRepository.findById(OWNER_USER_ID)).thenReturn(Optional.of(testUser));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> discussionService.addReaction(DISCUSSION_ID, OWNER_USER_ID, "Dit is een kut reactie")
        );

        assertEquals("Reactie afgekeurd vanwege ongepast taalgebruik.", exception.getMessage());
        assertEquals(0, testDiscussion.getReactionsCount());
    }

    // Test: Discussie verwijderen als eigenaar
    @Test
    @DisplayName("Discussie verwijderen als eigenaar")
    void deleteDiscussion_AsOwner_Success() {
        when(discussionRepository.findById(DISCUSSION_ID)).thenReturn(Optional.of(testDiscussion));

        discussionService.deleteDiscussion(DISCUSSION_ID, OWNER_USER_ID);

        verify(reactionRepository, times(1)).deleteAllByDiscussion_Id(DISCUSSION_ID);
        verify(discussionRepository, times(1)).delete(testDiscussion);
    }
}
