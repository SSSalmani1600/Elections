package nl.hva.election_backend.service;

// JUnit 5 imports voor testing
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Mockito imports voor mocking
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Project imports
import nl.hva.election_backend.dto.DiscussionDetailDto;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.repository.TestRepository;

// Java imports
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

// Static imports voor assertions en mockito
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests voor DiscussionService - Topic bewerken functionaliteit
 * 
 * Test scenarios:
 * 1. Happy path: eigenaar kan eigen topic bewerken
 * 2. Unhappy path: niet-eigenaar kan geen topic bewerken
 * 3. Unhappy path: topic niet gevonden
 */
@ExtendWith(MockitoExtension.class)
class DiscussionServiceTest {

    // Mock repositories - echte database wordt niet gebruikt
    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private TestRepository userRepository;

    // Service die we testen - wordt handmatig aangemaakt met mock repositories
    private DiscussionService discussionService;

    // Test data
    private DiscussionEntity testDiscussion;
    private User testUser;
    private final Long DISCUSSION_ID = 1L;
    private final Long OWNER_USER_ID = 100L;
    private final Long OTHER_USER_ID = 999L;

    /**
     * Setup: maak test data aan voor elke test
     */
    @BeforeEach
    void setUp() {
        // Maak de service aan met de gemockte repositories
        discussionService = new DiscussionService(
                discussionRepository,
                reactionRepository,
                userRepository
        );

        // Maak test user aan
        testUser = new User();
        testUser.setId(OWNER_USER_ID);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.nl");

        // Maak test discussie aan
        testDiscussion = new DiscussionEntity();
        testDiscussion.setId(DISCUSSION_ID);
        testDiscussion.setTitle("Originele Titel");
        testDiscussion.setBody("Originele inhoud van het topic");
        testDiscussion.setUserId(OWNER_USER_ID);  // Eigenaar is user 100
        testDiscussion.setCreatedAt(Instant.now());
        testDiscussion.setLastActivityAt(Instant.now());
        testDiscussion.setReactionsCount(0);
    }

    /**
     * Test: Eigenaar kan zijn eigen topic succesvol bewerken
     * 
     * Given: Een bestaande discussie van user 100
     * When: User 100 bewerkt de discussie
     * Then: De discussie wordt bijgewerkt en opgeslagen
     */
    @Test
    @DisplayName("Eigenaar kan eigen topic bewerken - Happy Path")
    void updateDiscussion_AsOwner_Success() {
        // Arrange: setup mock gedrag
        when(discussionRepository.findById(DISCUSSION_ID))
                .thenReturn(Optional.of(testDiscussion));
        when(discussionRepository.save(any(DiscussionEntity.class)))
                .thenReturn(testDiscussion);
        when(userRepository.findById(OWNER_USER_ID))
                .thenReturn(Optional.of(testUser));
        when(reactionRepository.findAllByDiscussion_IdOrderByCreatedAtAsc(DISCUSSION_ID))
                .thenReturn(Collections.emptyList());

        String newTitle = "Nieuwe Titel";
        String newBody = "Nieuwe inhoud van het topic";

        // Act: voer de update uit
        DiscussionDetailDto result = discussionService.updateDiscussion(
                DISCUSSION_ID,
                OWNER_USER_ID,  // Eigenaar bewerkt
                newTitle,
                newBody
        );

        // Assert: controleer resultaat
        assertNotNull(result, "Resultaat mag niet null zijn");
        
        // Verify: controleer dat save werd aangeroepen
        verify(discussionRepository, times(1)).save(any(DiscussionEntity.class));
        verify(discussionRepository, times(2)).findById(DISCUSSION_ID); // 1x voor update, 1x voor getDetailById
    }

    /**
     * Test: Niet-eigenaar kan topic NIET bewerken
     * 
     * Given: Een bestaande discussie van user 100
     * When: User 999 (niet de eigenaar) probeert te bewerken
     * Then: Er wordt een SecurityException gegooid
     */
    @Test
    @DisplayName("Niet-eigenaar kan geen topic bewerken - Security Check")
    void updateDiscussion_AsNonOwner_ThrowsSecurityException() {
        // Arrange: setup mock gedrag
        when(discussionRepository.findById(DISCUSSION_ID))
                .thenReturn(Optional.of(testDiscussion));

        // Act & Assert: verwacht SecurityException
        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> discussionService.updateDiscussion(
                        DISCUSSION_ID,
                        OTHER_USER_ID,  // NIET de eigenaar!
                        "Nieuwe Titel",
                        "Nieuwe Body"
                )
        );

        // Controleer foutmelding
        assertEquals("Je kunt alleen je eigen discussies bewerken", exception.getMessage());
        
        // Verify: save mag NIET aangeroepen zijn
        verify(discussionRepository, never()).save(any(DiscussionEntity.class));
    }

    /**
     * Test: Topic bewerken dat niet bestaat
     * 
     * Given: Geen discussie met ID 999
     * When: Iemand probeert discussie 999 te bewerken
     * Then: Er wordt een IllegalArgumentException gegooid
     */
    @Test
    @DisplayName("Topic niet gevonden - 404 scenario")
    void updateDiscussion_NotFound_ThrowsIllegalArgumentException() {
        // Arrange: discussie bestaat niet
        Long nonExistentId = 999L;
        when(discussionRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        // Act & Assert: verwacht IllegalArgumentException
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> discussionService.updateDiscussion(
                        nonExistentId,
                        OWNER_USER_ID,
                        "Nieuwe Titel",
                        "Nieuwe Body"
                )
        );

        // Controleer foutmelding
        assertEquals("Discussie niet gevonden", exception.getMessage());
        
        // Verify: save mag NIET aangeroepen zijn
        verify(discussionRepository, never()).save(any(DiscussionEntity.class));
    }

    /**
     * Test: lastActivityAt wordt geüpdatet bij bewerken
     * 
     * Given: Een bestaande discussie
     * When: De discussie wordt bewerkt
     * Then: De lastActivityAt timestamp wordt bijgewerkt
     */
    @Test
    @DisplayName("LastActivityAt wordt bijgewerkt bij bewerken")
    void updateDiscussion_UpdatesLastActivityAt() {
        // Arrange
        Instant oldActivityTime = Instant.parse("2024-01-01T00:00:00Z");
        testDiscussion.setLastActivityAt(oldActivityTime);
        
        when(discussionRepository.findById(DISCUSSION_ID))
                .thenReturn(Optional.of(testDiscussion));
        when(discussionRepository.save(any(DiscussionEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // Return saved entity
        when(userRepository.findById(OWNER_USER_ID))
                .thenReturn(Optional.of(testUser));
        when(reactionRepository.findAllByDiscussion_IdOrderByCreatedAtAsc(DISCUSSION_ID))
                .thenReturn(Collections.emptyList());

        // Act
        discussionService.updateDiscussion(
                DISCUSSION_ID,
                OWNER_USER_ID,
                "Nieuwe Titel",
                "Nieuwe Body"
        );

        // Assert: lastActivityAt moet nieuwer zijn dan de oude tijd
        assertTrue(
                testDiscussion.getLastActivityAt().isAfter(oldActivityTime),
                "lastActivityAt moet bijgewerkt zijn naar een nieuwere tijd"
        );
    }
}

