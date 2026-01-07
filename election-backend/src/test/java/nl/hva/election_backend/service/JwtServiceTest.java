package nl.hva.election_backend.service;

import nl.hva.election_backend.exception.InvalidRefreshTokenException;
import nl.hva.election_backend.model.RefreshToken;
import nl.hva.election_backend.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private RefreshTokenRepository refreshRepo;

    @InjectMocks
    private JwtService jwtService;

    private final Long userId = 123L;

    @BeforeEach
    void setUp() {
        // Set a valid base64 secret key for tests
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", "dGhpcy1pcy1hLXZlcnktc2VjdXJlLWJhc2U2NC1lbmNvZGVkLXNlY3JldC1rZXk=");
    }

    @Test
    void generateToken_CreatesValidToken() {
        // Act
        String token = jwtService.generateToken(userId);

        // Assert
        assertNotNull(token);
        assertFalse(token.isBlank());

        // Validate
        assertTrue(jwtService.validateToken(token));
        assertEquals(String.valueOf(userId), jwtService.extractUserId(token));
    }

    @Test
    void validateToken_WhenTokenIsInvalid_ReturnsFalse() {
        // Arrange
        String invalidToken = "invalid.token.string";

        // Act
        boolean isValid = jwtService.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WhenTokenIsNull_ReturnsFalse() {
        assertFalse(jwtService.validateToken(null));
        assertFalse(jwtService.validateToken(""));
    }

    // --- Refresh Token Generation (Hashing) Tests ---

    @Test
    void generateRefreshToken_ReturnsHashString() throws Exception {
        // Act
        String tokenHash = jwtService.generateRefreshToken();

        // Assert
        assertNotNull(tokenHash);
        assertFalse(tokenHash.isBlank());
        // SHA-256 hex string is always 64 characters
        assertEquals(64, tokenHash.length());
    }

    // --- Refresh Token Rotation Tests ---

    @Test
    void rotateRefreshToken_Success_RotatesTokens() throws Exception {
        // Arrange
        String oldHash = "oldHash123";
        Long userId = 1L;
        UUID familyId = UUID.randomUUID();

        // Mock the existing token in the DB
        RefreshToken oldToken = new RefreshToken();
        oldToken.setUserId(userId);
        oldToken.setTokenHash(oldHash);
        oldToken.setFamilyId(familyId);
        oldToken.setRevokedAt(null); // Explicitly active

        // Find the old token
        when(refreshRepo.findByTokenHash(oldHash)).thenReturn(Optional.of(oldToken));

        // Check if valid
        when(refreshRepo.existsByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(eq(oldHash), any(Instant.class)))
                .thenReturn(true);

        // Act
        RefreshToken newToken = jwtService.rotateRefreshToken(oldHash);

        // Assert
        assertNotNull(newToken);
        assertNotEquals(oldHash, newToken.getTokenHash()); // Hash must change
        assertEquals(userId, newToken.getUserId());
        assertEquals(familyId, newToken.getFamilyId()); // Must inherit family ID

        // Verify old token was revoked in memory
        assertNotNull(oldToken.getRevokedAt());

        // Verify both tokens were saved to DB
        verify(refreshRepo).saveAllAndFlush(any(List.class));
    }

    @Test
    void rotateRefreshToken_WhenTokenNotFound_ThrowsException() {
        // Arrange
        String nonExistentHash = "missing";
        when(refreshRepo.findByTokenHash(nonExistentHash)).thenReturn(Optional.empty());

        // Act & Assert
        InvalidRefreshTokenException ex = assertThrows(InvalidRefreshTokenException.class, () -> {
            jwtService.rotateRefreshToken(nonExistentHash);
        });

        assertEquals("Empty refresh token", ex.getMessage());
        verify(refreshRepo, never()).saveAllAndFlush(any());
    }

    @Test
    void rotateRefreshToken_WhenTokenInvalidOrExpired_ThrowsException() {
        // Arrange
        String oldHash = "expiredHash";
        RefreshToken oldToken = new RefreshToken();
        oldToken.setTokenHash(oldHash);

        // Token exists
        when(refreshRepo.findByTokenHash(oldHash)).thenReturn(Optional.of(oldToken));

        // Validation fails (e.g., expired or already revoked)
        when(refreshRepo.existsByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(eq(oldHash), any(Instant.class)))
                .thenReturn(false);

        // Act & Assert
        InvalidRefreshTokenException ex = assertThrows(InvalidRefreshTokenException.class, () -> {
            jwtService.rotateRefreshToken(oldHash);
        });

        assertEquals("Invalid or expired refresh token", ex.getMessage());
        verify(refreshRepo, never()).saveAllAndFlush(any());
    }
}