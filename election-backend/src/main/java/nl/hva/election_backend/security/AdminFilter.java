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
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (!uri.startsWith("/admin")) {
            filterChain.doFilter(request, response);
            return;
        }

        // JwtFilter zet userId als Integer, GEEN String
        Object userIdObj = request.getAttribute("userId");

        if (userIdObj == null) {
            deny(response);
            return;
        }

        Integer userId;
        try {
            userId = (Integer) userIdObj; // correcte cast
        } catch (ClassCastException e) {
            deny(response);
            return;
        }

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
        if (response.isCommitted()) return;

        response.resetBuffer();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        response.getWriter().write("""
            {"error": "forbidden", "message": "Admin access required"}
        """);
    }
}
