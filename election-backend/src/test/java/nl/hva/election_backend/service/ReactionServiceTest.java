package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.ModerationResult;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private ModerationService moderationService;

    @InjectMocks
    private ReactionService reactionService;


    @Test
    void addReaction_whenValidMessage_savesReaction() {

        DiscussionEntity discussion = new DiscussionEntity();
        discussion.setId(1L);

        when(discussionRepository.findById(1L)).thenReturn(Optional.of(discussion));

        ModerationResult moderationResult = new ModerationResult(
                "Dit is een reactie",
                "PENDING",
                false,
                false,
                List.<String>of()
        );

        when(moderationService.moderateText(anyString())).thenReturn(moderationResult);
        when(reactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));


        ReactionEntity result = reactionService.addReaction(1L, 10L, "Dit is een reactie");


        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(moderationService).moderateText(captor.capture());
        assertEquals("Dit is een reactie", captor.getValue()); // zo zie je meteen wat hij doorgeeft

        assertNotNull(result);
        assertEquals("Dit is een reactie", result.getMessage());
    }

    @Test
    void addReaction_whenDiscussionNotFound_throwsException() {

        when(discussionRepository.findById(1L))
                .thenReturn(Optional.empty());


        assertThrows(
                IllegalArgumentException.class,
                () -> reactionService.addReaction(1L, 1L, "test")
        );

        verify(reactionRepository, never()).save(any());
    }

    @Test
    void addReaction_whenFlagged_setsFlaggedReason() {

        DiscussionEntity discussion = new DiscussionEntity();
        discussion.setId(1L);

        when(discussionRepository.findById(1L))
                .thenReturn(Optional.of(discussion));

        ModerationResult moderationResult =
                new ModerationResult(
                        "gecensureerde tekst",
                        "FLAGGED",
                        true,
                        false,
                        List.of("beledigend")
                );

        when(moderationService.moderateText("test"))
                .thenReturn(moderationResult);

        when(reactionRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        ReactionEntity result =
                reactionService.addReaction(1L, 2L, "test");

        // Assert
        assertEquals("FLAGGED", result.getModerationStatus());
        assertEquals("beledigend", result.getFlaggedReason());
        assertEquals("gecensureerde tekst", result.getMessage());
    }

    @Test
    void updateReaction_whenOwner_updatesMessage() {

        ReactionEntity reaction = new ReactionEntity();
        reaction.setUserId(5L);
        reaction.setMessage("oud");

        when(reactionRepository.findById(1L))
                .thenReturn(Optional.of(reaction));

        when(reactionRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));


        ReactionEntity result =
                reactionService.updateReaction(1L, 5L, "nieuw");


        assertEquals("nieuw", result.getMessage());
        verify(reactionRepository).save(reaction);
    }

    @Test
    void updateReaction_whenNotOwner_throwsSecurityException() {

        ReactionEntity reaction = new ReactionEntity();
        reaction.setUserId(1L);

        when(reactionRepository.findById(1L))
                .thenReturn(Optional.of(reaction));


        assertThrows(
                SecurityException.class,
                () -> reactionService.updateReaction(1L, 99L, "nieuw")
        );

        verify(reactionRepository, never()).save(any());
    }

    @Test
    void updateReaction_whenNotFound_throwsException() {

        when(reactionRepository.findById(1L))
                .thenReturn(Optional.empty());


        assertThrows(
                IllegalArgumentException.class,
                () -> reactionService.updateReaction(1L, 1L, "nieuw")
        );
    }


    @Test
    void deleteReaction_whenOwner_deletesReactionAndUpdatesCount() {
        // Arrange
        DiscussionEntity discussion = new DiscussionEntity();
        discussion.setReactionsCount(3);

        ReactionEntity reaction = new ReactionEntity();
        reaction.setUserId(10L);
        reaction.setDiscussion(discussion);

        when(reactionRepository.findById(1L))
                .thenReturn(Optional.of(reaction));


        reactionService.deleteReaction(1L, 10L);


        verify(reactionRepository).delete(reaction);
        verify(discussionRepository).save(discussion);
        assertEquals(2, discussion.getReactionsCount());
    }

    @Test
    void deleteReaction_whenNotOwner_throwsSecurityException() {

        ReactionEntity reaction = new ReactionEntity();
        reaction.setUserId(1L);

        when(reactionRepository.findById(1L))
                .thenReturn(Optional.of(reaction));


        assertThrows(
                SecurityException.class,
                () -> reactionService.deleteReaction(1L, 99L)
        );

        verify(reactionRepository, never()).delete(any());
    }

    @Test
    void deleteReaction_whenNotFound_throwsException() {

        when(reactionRepository.findById(1L))
                .thenReturn(Optional.empty());


        assertThrows(
                IllegalArgumentException.class,
                () -> reactionService.deleteReaction(1L, 1L)
        );
    }
}
