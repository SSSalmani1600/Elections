package nl.hva.election_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.hva.election_backend.service.JwtService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1) // 🔥 JwtFilter moet ALTIJD vóór AdminFilter draaien
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // Public endpoints
    private final String[] whiteListPatterns = {
            ".*/api/auth(/.*)?",
            ".*/api/parties(/.*)?",
            ".*/api/elections(/.*)?",
            ".*/api/users(/.*)?",
            ".*/api/next-elections(/.*)?"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI();
        System.out.println("[JwtFilter] Incoming request URI: " + uri);

        // Check whitelist
        for (String pattern : whiteListPatterns) {
            if (uri.matches(pattern)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Public GET/POST on discussions
        if (uri.startsWith("/api/discussions")
                && ("GET".equalsIgnoreCase(request.getMethod())
                || "POST".equalsIgnoreCase(request.getMethod()))) {
            filterChain.doFilter(request, response);
            return;
        }

        // JWT validation
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                if (token.isEmpty() || !jwtService.validateToken(token)) {
                    unauthorized(response, "invalid_token", "Invalid or expired JWT token");
                    return;
                }

                String displayName = jwtService.extractDisplayName(token);
                Integer userId = jwtService.extractUserId(token);

                request.setAttribute("userName", displayName);
                request.setAttribute("userId", String.valueOf(userId));

                filterChain.doFilter(request, response);
                return;

            } catch (Exception e) {
                unauthorized(response, "internal_error", e.getMessage());
                return;
            }
        }

        unauthorized(response, "missing_token", "Authorization: Bearer <token> required");
    }

    private void unauthorized(HttpServletResponse response, String code, String message) throws IOException {
        if (response.isCommitted()) return;

        response.resetBuffer();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("{\"error\":\"" + code + "\",\"message\":\"" + message + "\"}");
    }
}
