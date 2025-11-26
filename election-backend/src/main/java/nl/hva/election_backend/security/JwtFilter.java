package nl.hva.election_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.hva.election_backend.dto.TokenValidationResponse;
import nl.hva.election_backend.service.JwtService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // ⭐ GEZAMENLIJKE WHITELIST
    private static final Pattern[] whiteListPatterns = {
            Pattern.compile("^/api/auth.*$"),
            Pattern.compile("^/api/parties.*$"),
            Pattern.compile("^/api/elections.*$"),
            Pattern.compile("^/api/discussions.*$"),
            Pattern.compile("^/api/statements.*$"),
            Pattern.compile("^/api/next-elections.*$"),
            Pattern.compile("^/api/users.*$")
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Allow preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // ⭐ CHECK WHITELIST
        String uri = request.getRequestURI();
        for (Pattern pattern : whiteListPatterns) {
            if (pattern.matcher(uri).matches()) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // ⭐ 1. COOKIE BASED JWT (main backend)
        String cookieToken = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("jwt".equals(c.getName())) {
                    cookieToken = c.getValue();
                }
            }
        }

        // ⭐ 2. BEARER TOKEN (jouw versie)
        String authHeader = request.getHeader("Authorization");
        String bearerToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            bearerToken = authHeader.substring(7);
        }

        // ⭐ PRIORITEIT: Bearer → anders Cookie
        String token = bearerToken != null ? bearerToken : cookieToken;

        if (token == null || token.isEmpty()) {
            unauthorized(response, "missing_token", "This endpoint requires a valid JWT (cookie or Bearer).");
            return;
        }

        // ⭐ TOKEN VALIDATIE
        try {
            TokenValidationResponse validation = jwtService.validateToken(token);

            if (!validation.isValid()) {
                unauthorized(response, "invalid_token", "Invalid or expired JWT token");
                return;
            }

            // ⭐ Extract displayName + userId (jouw claim)
            Integer userId = null;
            String displayName = null;
            String username = null;

            try {
                userId = jwtService.extractUserId(token);
            } catch (Exception ignored) {}

            try {
                displayName = jwtService.extractDisplayName(token);
            } catch (Exception ignored) {}

            try {
                username = jwtService.extractUsername(token);
            } catch (Exception ignored) {}

            // ⭐ Request attributes beschikbaar voor controllers
            if (username != null) request.setAttribute("username", username);
            if (displayName != null) request.setAttribute("userName", displayName);
            if (userId != null) request.setAttribute("userId", userId);

            filterChain.doFilter(request, response);

        } catch (RuntimeException e) {  // 💡 FIX: alleen RuntimeException
            unauthorized(response, "internal_error", e.getMessage());
        }
    }

    private void unauthorized(HttpServletResponse response, String code, String message) throws IOException {
        if (response.isCommitted()) return;

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"error\":\"" + code + "\",\"message\":\"" + message + "\"}"
        );
        response.flushBuffer();
    }
}
