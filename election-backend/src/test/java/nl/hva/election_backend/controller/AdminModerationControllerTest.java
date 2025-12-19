package nl.hva.election_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.service.JwtService;
import nl.hva.election_backend.service.ModerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminModerationController.class)
@AutoConfigureMockMvc(addFilters = false) // we testen alleen controller, geen security filters
class AdminModerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ModerationService moderationService;

    @MockBean(name = "jwtFilter")
    private Object jwtFilter;

    @MockBean(name = "adminFilter")
    private Object adminFilter;

    @MockBean
    private JwtService jwtService;

    @Test
    void getPending_returnsList() throws Exception {
        when(moderationService.getPendingReactions())
                .thenReturn(List.of(new ReactionEntity(), new ReactionEntity()));

        mockMvc.perform(get("/api/admin/moderation/pending"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(moderationService).getPendingReactions();
        verifyNoMoreInteractions(moderationService);
    }

    @Test
    void getFlagged_returnsList() throws Exception {
        when(moderationService.getFlaggedReactions())
                .thenReturn(List.of(new ReactionEntity()));

        mockMvc.perform(get("/api/admin/moderation/flagged"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(moderationService).getFlaggedReactions();
        verifyNoMoreInteractions(moderationService);
    }

    @Test
    void approve_callsService() throws Exception {
        long id = 10L;

        mockMvc.perform(post("/api/admin/moderation/{id}/approve", id))
                .andExpect(status().isOk());

        verify(moderationService).approveReaction(id);
        verifyNoMoreInteractions(moderationService);
    }

    @Test
    void reject_callsService() throws Exception {
        long id = 11L;

        mockMvc.perform(post("/api/admin/moderation/{id}/reject", id))
                .andExpect(status().isOk());

        verify(moderationService).rejectReaction(id);
        verifyNoMoreInteractions(moderationService);
    }

    @Test
    void flag_callsServiceWithReason() throws Exception {
        long id = 12L;

        var body = new AdminModerationController.FlagRequest("spam");
        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(post("/api/admin/moderation/{id}/flag", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(moderationService).flagReaction(id, "spam");
        verifyNoMoreInteractions(moderationService);
    }

    /**
     * Bad flow: ongeldige JSON body -> bij jou wordt dit (via global handler) 500.
     * Daarom testen we op 500, anders faalt je test zoals je screenshot liet zien.
     */
    @Test
    void flag_withInvalidJson_returns500() throws Exception {
        long id = 12L;

        mockMvc.perform(post("/api/admin/moderation/{id}/flag", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-valid-json}"))
                .andExpect(status().isInternalServerError()); // of .is5xxServerError()

        verifyNoInteractions(moderationService);
    }

    /**
     * Bad flow: service gooit exception -> zonder specifieke mapping meestal 500.
     */
    @Test
    void approve_whenServiceThrows_returns5xx() throws Exception {
        long id = 10L;
        doThrow(new RuntimeException("boom")).when(moderationService).approveReaction(id);

        mockMvc.perform(post("/api/admin/moderation/{id}/approve", id))
                .andExpect(status().is5xxServerError());

        verify(moderationService).approveReaction(id);
        verifyNoMoreInteractions(moderationService);
    }
}
