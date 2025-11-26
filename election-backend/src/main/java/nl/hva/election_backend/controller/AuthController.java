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
    public ResponseEntity<?> fetchUser(@CookieValue(value = "jwt", required = false) String accessToken) {
        User user;
        try {
            user = authService.getUser(accessToken);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(401).build();
        }

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
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        if (req.getEmail().isEmpty() || req.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid email or password");
        }

        AuthenticationResponse authResponse = authService.authenticate(req.getEmail(), req.getPassword());
        User user = authResponse.getUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
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
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User user = authService.register(req.getEmail(), req.getPassword(), req.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new RegisterResponse(user.getEmail(), req.getPassword(), user.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registratie mislukt");
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refresh_token", required = false) String refreshTokenHash) {
        if (refreshTokenHash == null || refreshTokenHash.isEmpty()) return ResponseEntity
                .status(401).body("Empty refresh token");

        TokenRefreshResponse tokenPair;

        try {
            tokenPair = authService.refreshTokens(refreshTokenHash);
        } catch(InvalidRefreshTokenException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }

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
}
