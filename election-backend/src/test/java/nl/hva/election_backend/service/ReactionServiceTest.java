package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.ModerationResult;
import nl.hva.election_backend.dto.ReactionDto;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"DataFlowIssue", "null"})
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;
    @Mock
    private DiscussionRepository discussionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModerationService moderationService;

    private ReactionService reactionService;
    private DiscussionEntity testDiscussion;
    private User testUser;

    @BeforeEach
    void setUp() {
        reactionService = new ReactionService(reactionRepository, discussionRepository, userRepository, moderationService);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        
        testDiscussion = new DiscussionEntity();
        testDiscussion.setId(1L);
        testDiscussion.setReactionsCount(0);
    }

    @Test
    @DisplayName("Reactie toevoegen - succes")
    void addReaction_Success() {
        when(discussionRepository.findById(1L)).thenReturn(Optional.of(Objects.requireNonNull(testDiscussion)));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(moderationService.moderateText(anyString())).thenAnswer(i -> {
            ModerationResult res = new ModerationResult(i.getArgument(0, String.class));
            res.setModerationStatus("APPROVED");
            return res;
        });
        when(reactionRepository.save(any(ReactionEntity.class))).thenAnswer(i -> {
            ReactionEntity r = Objects.requireNonNull(i.getArgument(0, ReactionEntity.class));
            r.setId(100L);
            return r;
        });

        ReactionDto result = reactionService.addReaction(1L, 1L, "Goeie post!");

        assertNotNull(result);
        assertEquals("Goeie post!", result.getMessage());
        assertEquals(1, testDiscussion.getReactionsCount());
        verify(discussionRepository).save(testDiscussion);
    }

    @Test
    @DisplayName("Reactie toevoegen - discussie niet gevonden")
    void addReaction_DiscussionNotFound_ThrowsException() {
        when(discussionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reactionService.addReaction(1L, 1L, "Test"));
    }

    @Test
    @DisplayName("Reactie verwijderen door niet-eigenaar - Throws SecurityException")
    void deleteReaction_NotOwner_ThrowsSecurityException() {
        ReactionEntity reaction = new ReactionEntity();
        User owner = new User();
        owner.setId(1L);
        reaction.setUser(owner);
        
        when(reactionRepository.findById(100L)).thenReturn(Optional.of(Objects.requireNonNull(reaction)));

        assertThrows(SecurityException.class, () -> reactionService.deleteReaction(100L, 2L));
    }

    @Test
    @DisplayName("Reactie toevoegen - geblokkeerd door moderatie")
    void addReaction_Blocked_ThrowsForbidden() {
        when(discussionRepository.findById(1L)).thenReturn(Optional.of(Objects.requireNonNull(testDiscussion)));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(moderationService.moderateText(anyString())).thenReturn(
                new ModerationResult("bad", "BLOCKED", false, true, java.util.List.of("forbidden"))
        );

        assertThrows(ForbiddenException.class, () -> reactionService.addReaction(1L, 1L, "bad"));

        verify(reactionRepository, never()).save(any());
        verify(discussionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Reactie toevoegen - flagged slaat status en reden op")
    void addReaction_Flagged_SetsFlaggedReason() {
        when(discussionRepository.findById(1L)).thenReturn(Optional.of(Objects.requireNonNull(testDiscussion)));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ModerationResult flagged = new ModerationResult(
                "origineel",
                "FLAGGED",
                true,
                false,
                java.util.List.of("beledigend")
        );
        flagged.setModeratedText("gecensureerd");

        when(moderationService.moderateText(anyString())).thenReturn(flagged);

        when(reactionRepository.save(any(ReactionEntity.class))).thenAnswer(i -> Objects.requireNonNull(i.getArgument(0, ReactionEntity.class)));

        ReactionDto dto = reactionService.addReaction(1L, 1L, "origineel");

        ArgumentCaptor<ReactionEntity> captor = ArgumentCaptor.forClass(ReactionEntity.class);
        verify(reactionRepository).save(captor.capture());

        ReactionEntity saved = Objects.requireNonNull(captor.getValue());
        assertEquals("FLAGGED", saved.getModerationStatus());
        assertEquals("beledigend", saved.getFlaggedReason());
        assertEquals("gecensureerd", dto.getMessage());
    }

    @Test
    @DisplayName("Reactie bijwerken - niet eigenaar")
    void updateReaction_NotOwner_ThrowsSecurity() {
        ReactionEntity reaction = new ReactionEntity();
        User owner = new User();
        owner.setId(1L);
        reaction.setUser(owner);

        when(reactionRepository.findById(10L)).thenReturn(Optional.of(Objects.requireNonNull(reaction)));

        assertThrows(SecurityException.class, () -> reactionService.updateReaction(10L, 2L, "nieuw"));
        verify(reactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Reactie bijwerken - geblokkeerd door moderatie")
    void updateReaction_Blocked_ThrowsForbidden() {
        ReactionEntity reaction = new ReactionEntity();
        User owner = new User();
        owner.setId(1L);
        reaction.setUser(owner);

        when(reactionRepository.findById(10L)).thenReturn(Optional.of(Objects.requireNonNull(reaction)));
        when(moderationService.moderateText(anyString())).thenReturn(
                new ModerationResult("nieuw", "BLOCKED", false, true, java.util.List.of("forbidden"))
        );

        assertThrows(ForbiddenException.class, () -> reactionService.updateReaction(10L, 1L, "nieuw"));
        verify(reactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Reactie verwijderen - niet gevonden")
    void deleteReaction_NotFound_ThrowsResourceNotFound() {
        when(reactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reactionService.deleteReaction(99L, 1L));
    }

    @Test
    @DisplayName("Reactie verwijderen - eigenaar, teller omlaag")
    void deleteReaction_Owner_UpdatesCount() {
        DiscussionEntity discussion = new DiscussionEntity();
        discussion.setReactionsCount(2);

        ReactionEntity reaction = new ReactionEntity();
        reaction.setDiscussion(discussion);
        User owner = new User();
        owner.setId(5L);
        reaction.setUser(owner);

        when(reactionRepository.findById(20L)).thenReturn(Optional.of(Objects.requireNonNull(reaction)));

        reactionService.deleteReaction(20L, 5L);

        verify(reactionRepository).delete(reaction);
        verify(discussionRepository).save(discussion);
        assertEquals(1, discussion.getReactionsCount());
    }
}
