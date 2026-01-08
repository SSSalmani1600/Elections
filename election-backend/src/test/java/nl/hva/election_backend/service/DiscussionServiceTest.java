package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.*;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.exception.ForbiddenException;
import nl.hva.election_backend.exception.ResourceNotFoundException;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscussionServiceTest {

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModerationService moderationService;

    private DiscussionService discussionService;

    private DiscussionEntity testDiscussion;
    private User testUser;
    private final Long DISCUSSION_ID = 1L;
    private final Long OWNER_USER_ID = 100L;
    private final Long OTHER_USER_ID = 999L;

    @BeforeEach
    void setUp() {
        discussionService = new DiscussionService(
                discussionRepository,
                reactionRepository,
                userRepository,
                moderationService
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

    @Test
    @DisplayName("Lijst van discussies ophalen (gepagineerd) - succes")
    void list_Success() {
        Page<DiscussionEntity> page = new PageImpl<>(Collections.singletonList(testDiscussion));
        when(discussionRepository.findAllWithUser(any(Pageable.class))).thenReturn(page);

        PageResponseDto<DiscussionListItemDto> result = discussionService.list(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Originele Titel", result.getContent().get(0).title());
    }

    @Test
    @DisplayName("Eigenaar kan eigen topic bewerken - succes")
    void updateDiscussion_AsOwner_Success() {
        UpdateDiscussionRequest request = new UpdateDiscussionRequest();
        request.setUserId(OWNER_USER_ID);
        request.setTitle("Nieuwe Titel");
        request.setBody("Nieuwe Body");

        when(discussionRepository.findById(DISCUSSION_ID)).thenReturn(Optional.of(testDiscussion));
        when(moderationService.moderateText(anyString())).thenAnswer(i -> new ModerationResult((String) i.getArgument(0)));
        when(reactionRepository.findAllByDiscussion_IdOrderByCreatedAtAsc(DISCUSSION_ID)).thenReturn(Collections.emptyList());

        DiscussionDetailDto result = discussionService.updateDiscussion(DISCUSSION_ID, request);

        assertNotNull(result);
        assertEquals("Nieuwe Titel", testDiscussion.getTitle());
        verify(discussionRepository).save(testDiscussion);
    }

    @Test
    @DisplayName("Niet-eigenaar kan geen topic bewerken - error flow")
    void updateDiscussion_AsNonOwner_ThrowsSecurityException() {
        UpdateDiscussionRequest request = new UpdateDiscussionRequest();
        request.setUserId(OTHER_USER_ID);
        request.setTitle("Nieuwe Titel");
        request.setBody("Nieuwe Body");

        when(discussionRepository.findById(DISCUSSION_ID)).thenReturn(Optional.of(testDiscussion));

        assertThrows(SecurityException.class, () -> discussionService.updateDiscussion(DISCUSSION_ID, request));
    }

    @Test
    @DisplayName("Discussie aanmaken met geblokkeerde woorden - error flow")
    void createDiscussion_BlockedContent_ThrowsForbiddenException() {
        CreateDiscussionRequest request = new CreateDiscussionRequest();
        request.setUserId(OWNER_USER_ID);
        request.setTitle("Slecht woord");
        request.setBody("Inhoud");

        ModerationResult blocked = new ModerationResult("Slecht woord");
        blocked.setBlocked(true);
        when(moderationService.moderateText("Slecht woord")).thenReturn(blocked);

        assertThrows(ForbiddenException.class, () -> discussionService.createDiscussion(request));
    }

    @Test
    @DisplayName("Discussie verwijderen als eigenaar - succes")
    void deleteDiscussion_AsOwner_Success() {
        when(discussionRepository.findById(DISCUSSION_ID)).thenReturn(Optional.of(testDiscussion));

        discussionService.deleteDiscussion(DISCUSSION_ID, OWNER_USER_ID);

        verify(reactionRepository).deleteAllByDiscussion_Id(DISCUSSION_ID);
        verify(discussionRepository).delete(testDiscussion);
    }
}
