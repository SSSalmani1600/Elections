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

    private static final Pattern[] whiteListPatterns = {
            Pattern.compile("^/api/auth(/.*)?$"),
            Pattern.compile("^/api/parties(/.*)?$"),
            Pattern.compile("^/api/elections(/.*)?$"),
            Pattern.compile("^/api/discussions(/.*)?$")
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Allow preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Allow whitelisted endpoints
        String uri = request.getRequestURI();
        for (Pattern pattern : whiteListPatterns) {
            if (pattern.matcher(uri).matches()) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String jwtToken = null;

        // 1. Probeer eerst de Authorization header (Bearer token)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        }

        // 2. Als geen header, probeer de cookie
        if (jwtToken == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        jwtToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (jwtToken == null) {
            unauthorized(response, "missing_token", "Request to secured endpoint requires token");
            return;
        }

        try {
            TokenValidationResponse tokenResponse = jwtService.validateToken(jwtToken);

            if (!tokenResponse.isValid()) {
                unauthorized(response, "invalid_token", "Invalid or expired JWT token");
                return;
            }

//            if (tokenResponse.shouldRefresh()) {
//                return;
//            }

            String username = jwtService.extractUsername(jwtToken);
            request.setAttribute("username", username);
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            unauthorized(response, "internal_error", String.valueOf(e));
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

