package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.*;
import nl.hva.election_backend.exception.InvalidRefreshTokenException;
import nl.hva.election_backend.exception.UnauthorizedException;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/session")
    public ResponseEntity<LoginResponse> fetchUser(@CookieValue(value = "jwt", required = false) String accessToken) {
        User user = authService.getUser(accessToken);

        return ResponseEntity.ok(
                new LoginResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        user.getIsAdmin()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {

        if (req.getEmail().isEmpty() || req.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        AuthenticationResponse authResponse = authService.authenticate(req.getEmail(), req.getPassword());
        User user = authResponse.getUser();

        if (user == null) {
            throw new UnauthorizedException("Invalid email or password");
        }

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 15)
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("jwt", authResponse.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 5)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(new LoginResponse(user.getId(), user.getEmail(), user.getUsername(), user.getIsAdmin()));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest req) {
        User user = authService.register(req.getEmail(), req.getPassword(), req.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(user.getEmail(), req.getPassword(), user.getUsername()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(@CookieValue(value = "refresh_token", required = false) String refreshTokenHash) {
        if (refreshTokenHash == null || refreshTokenHash.isEmpty()) {
            throw new UnauthorizedException("Empty refresh token");
        }

        TokenRefreshResponse tokenPair = authService.refreshTokens(refreshTokenHash);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokenPair.getRefreshTokenHash())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 15)
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("jwt", tokenPair.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 5)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString()).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("jwt", "")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .build();
    }
}
