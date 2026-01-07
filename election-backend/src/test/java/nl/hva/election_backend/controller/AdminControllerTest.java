package nl.hva.election_backend.controller;

import nl.hva.election_backend.exception.GlobalExceptionHandler;
import nl.hva.election_backend.security.AdminFilter;
import nl.hva.election_backend.security.JwtFilter;
import nl.hva.election_backend.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AdminController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AdminFilter.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    void getAdminStats_returnsStats() throws Exception {
        when(adminService.getTotalUsers()).thenReturn(10);
        when(adminService.getReportedPosts()).thenReturn(3);
        when(adminService.getPendingReviews()).thenReturn(2);

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(10))
                .andExpect(jsonPath("$.reportedPosts").value(3))
                .andExpect(jsonPath("$.pendingReviews").value(2));
    }

    @Test
    void getAdminStats_whenServiceThrowsException_returns500() throws Exception {
        when(adminService.getTotalUsers()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isInternalServerError());
    }
}
