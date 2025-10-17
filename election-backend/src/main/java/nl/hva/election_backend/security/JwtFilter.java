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
import java.util.Arrays;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService = new JwtService();
    String[] whiteListURLs = {
            "/api/auth/",
            "/api/parties",
            "/api/elections/",
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Preflight doorlaten
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // White listed endpoints altijd doorlaten
        String uri = request.getRequestURI();
        for (String url : whiteListURLs) {
            if (uri.startsWith(url)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // ✅ Alleen GET en POST naar /api/discussions publiek maken voor demo/sprint review
        if (uri.startsWith("/api/discussions")
                && ("GET".equalsIgnoreCase(request.getMethod()) || "POST".equalsIgnoreCase(request.getMethod()))) {
            filterChain.doFilter(request, response);
            return;
        }

        // Normale JWT check
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            try {
                if (token.isEmpty() || !jwtService.validateToken(token)) {
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

        // Als geen geldige header → 401
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
