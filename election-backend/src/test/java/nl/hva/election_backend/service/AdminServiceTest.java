package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.repository.ReactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void getTotalUsers_returnsCorrectCount() {

        String sql = "SELECT COUNT(*) FROM public.users";
        when(jdbcTemplate.queryForObject(sql, Integer.class))
                .thenReturn(12);


        int result = adminService.getTotalUsers();


        assertEquals(12, result);
    }

    @Test
    void getReportedPosts_returnsCorrectCount() {

        String sql = "SELECT COUNT(*) FROM public.discussions";
        when(jdbcTemplate.queryForObject(sql, Integer.class))
                .thenReturn(4);


        int result = adminService.getReportedPosts();


        assertEquals(4, result);
    }

    @Test
    void getPendingReviews_returnsNumberOfPendingReactions() {

        ReactionEntity r1 = new ReactionEntity();
        ReactionEntity r2 = new ReactionEntity();
        ReactionEntity r3 = new ReactionEntity();

        when(reactionRepository.findByModerationStatus("PENDING"))
                .thenReturn(List.of(r1, r2, r3));


        int result = adminService.getPendingReviews();


        assertEquals(3, result);
    }


    @Test
    void getPendingReviews_returnsZeroWhenNoPendingReactions() {

        when(reactionRepository.findByModerationStatus("PENDING"))
                .thenReturn(List.of());


        int result = adminService.getPendingReviews();


        assertEquals(0, result);
    }
}
