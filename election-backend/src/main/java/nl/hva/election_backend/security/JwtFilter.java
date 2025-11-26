package nl.hva.election_backend.security;

import io.jsonwebtoken.JwtException;
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

    // ⭐ COMBINATIE VAN JOUW EN MAIN WHITELIST
    private static final Pattern[] whiteListPatterns = {
            Pattern.compile("^/api/auth(/.*)?$"),
            Pattern.compile("^/api/parties(/.*)?$"),
            Pattern.compile("^/api/elections(/.*)?$"),
            Pattern.compile("^/api/users(/.*)?$"),
            Pattern.compile("^/api/next-elections(/.*)?$"),
            Pattern.compile("^/api/discussions(/.*)?$"), // GET/POST public
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Allow preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // ⭐ WHITELISTING
        String uri = request.getRequestURI();
        for (Pattern pattern : whiteListPatterns) {
            if (pattern.matcher(uri).matches()) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // ⭐ SUPPORT VOOR COOKIE-GEBASEERDE JWT (from main)
        Cookie[] cookies = request.getCookies();
        String cookieToken = null;

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("jwt".equals(c.getName())) {
                    cookieToken = c.getValue();
                }
            }
        }

        // ⭐ SUPPORT VOOR BEARER TOKEN (from your version)
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

        // ⭐ VALIDATIE — combinatie van beide versies
        try {
            TokenValidationResponse validation = jwtService.validateToken(token);

            if (!validation.isValid()) {
                unauthorized(response, "invalid_token", "Invalid or expired JWT token");
                return;
            }

            // Extract data (support jouw extractions)
            String username = jwtService.extractUsername(token);
            Integer userId = jwtService.extractUserId(token);
            String displayName = jwtService.extractDisplayName(token);

            if (username != null) request.setAttribute("username", username);
            if (displayName != null) request.setAttribute("userName", displayName);
            if (userId != null) request.setAttribute("userId", userId);

            filterChain.doFilter(request, response);

        } catch (JwtException | RuntimeException e) {
            unauthorized(response, "internal_error", e.getMessage());
        }
    }

    private void unauthorized(HttpServletResponse response, String code, String message) throws IOException {
        if (response.isCommitted()) return;

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + code + "\",\"message\":\"" + message + "\"}");
        response.flushBuffer();
    }
}
