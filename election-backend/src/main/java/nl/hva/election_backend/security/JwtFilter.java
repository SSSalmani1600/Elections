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
import java.util.Arrays;
import java.util.Date;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService = new JwtService();
    String[] whiteListURLs = {
            "/api/auth/",
            "/api/parties",
            "/api/elections/",
            "/api/discussions"
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

        Cookie[] cookies = request.getCookies();
        String jwtToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
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

            if (tokenResponse.shouldRefresh()) {
                return;
            }

            String username = jwtService.extractDisplayName(jwtToken);
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
