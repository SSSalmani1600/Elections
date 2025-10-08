package nl.hva.election_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.hva.election_backend.service.JwtService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService = new JwtService();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 1) Preflight altijd doorlaten
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Publieke endpoints whitelisten
        boolean isAuthEndpoint = uri.startsWith("/api/auth/");
        boolean isPublicDiscussionsGet =
                "GET".equalsIgnoreCase(method) &&
                        ("/api/discussions".equals(uri) || uri.startsWith("/api/discussions/"));
        boolean isOtherPublic = "/favicon.ico".equals(uri) || uri.startsWith("/error");

        if (isAuthEndpoint || isPublicDiscussionsGet || isOtherPublic) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) Voor de rest: token vereist
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // na "Bearer "

            try {
                if (!jwtService.validateToken(token) || token.isEmpty()) {
                    unauthorized(response, "invalid_token", "Invalid or expired JWT token");
                    return;
                }

                String user = jwtService.extractDisplayName(token);
                request.setAttribute("userName", user);
                filterChain.doFilter(request, response);
                return;
            } catch (Exception e) {
                unauthorized(response, "internal_error", String.valueOf(e));
                return;
            }
        }

        unauthorized(response, "missing_token", "Authorization: Bearer <token> required");
    }

    private void unauthorized(HttpServletResponse response, String code, String message) throws IOException {
        if (response.isCommitted()) return;
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + code + "\",\"message\":\"" + message + "\"}");
        response.flushBuffer();
    }
}
