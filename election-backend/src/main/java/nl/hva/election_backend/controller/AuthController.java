package nl.hva.election_backend.controller;

import nl.hva.election_backend.dto.*;
import nl.hva.election_backend.model.RefreshToken;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.service.AuthService;
import nl.hva.election_backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        if (req.getEmail().isEmpty() || req.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid email or password");
        }

        // MAIN authenticatie flow (access + refresh token)
        AuthenticationResponse authResponse = authService.authenticate(req.getEmail(), req.getPassword());
        User user = authResponse.getUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        // Cookies zetten
        ResponseCookie accessCookie = ResponseCookie.from("jwt", authResponse.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(86400)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(86400)
                .build();

        // ⭐ Jouw toevoeging: LoginResponse moet ID + username + token + isAdmin bevatten
        LoginResponse response = new LoginResponse(
                user.getId(),
                authResponse.getAccessToken(),
                user.getUsername(),
                user.getIsAdmin() // <── ADMIN SUPPORT
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(response);
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

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue("refresh_token") String refreshToken,
            @CookieValue("jwt") String accessToken
    ) {
        if (refreshToken == null) return ResponseEntity.badRequest().build();

        RefreshToken newRefreshToken = jwtService.refreshToken(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", newRefreshToken.getTokenHash())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(86400)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }
}
