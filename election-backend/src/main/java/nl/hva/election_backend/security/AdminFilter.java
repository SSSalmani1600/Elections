package nl.hva.election_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(200)
public class AdminFilter extends OncePerRequestFilter {

    private final JdbcTemplate jdbcTemplate;

    public AdminFilter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Alleen admin endpoints beveiligen
        String uri = request.getRequestURI();
        if (!uri.startsWith("/admin")) {
            filterChain.doFilter(request, response);
            return;
        }

        // JwtFilter MOET dit zetten → request.setAttribute("userId", userId);
        String userIdAttr = (String) request.getAttribute("userId");

        if (userIdAttr == null) {
            deny(response);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdAttr);
        } catch (NumberFormatException e) {
            deny(response);
            return;
        }

        // Check of gebruiker admin is
        Boolean isAdmin = jdbcTemplate.queryForObject(
                "SELECT is_admin FROM public.users WHERE id = ?",
                Boolean.class,
                userId
        );

        if (isAdmin == null || !isAdmin) {
            deny(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void deny(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("""
            {"error": "forbidden", "message": "Admin access required"}
        """);
    }
}
