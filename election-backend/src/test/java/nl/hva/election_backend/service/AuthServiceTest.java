package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.AuthenticationResponse;
import nl.hva.election_backend.model.RefreshToken;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.RefreshTokenRepository;
import nl.hva.election_backend.repository.UserRepository;
import nl.hva.election_backend.security.BCryptPasswordHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private BCryptPasswordHasher hasher;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshRepo;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_Successful_ReturnsTokensAndUser() throws Exception {
        // Arrange
        String email = "test@hva.nl";
        String password = "password123";
        String encodedPassword = "encodedPassword";
        long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail(email);
        mockUser.setPasswordHash(encodedPassword);

        String expectedAccessToken = "access-token-123";
        String expectedRefreshToken = "refresh-token-456";

        // Mock dependencies
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(hasher.matches(password, encodedPassword)).thenReturn(true);
        when(jwtService.generateRefreshToken()).thenReturn(expectedRefreshToken);
        when(jwtService.generateToken(userId)).thenReturn(expectedAccessToken);

        // Act
        AuthenticationResponse response = authService.authenticate(email, password);

        // Assert
        assertNotNull(response);
        assertEquals(expectedAccessToken, response.getAccessToken());
        assertEquals(expectedRefreshToken, response.getRefreshToken());
        assertEquals(mockUser, response.getUser());

        verify(refreshRepo, times(1)).saveAndFlush(any(RefreshToken.class));
    }

    @Test
    void authenticate_UserNotFound_ReturnsEmptyResponse() {
        // Arrange
        String email = "nonexistent@hva.nl";
        String password = "password123";

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        AuthenticationResponse response = authService.authenticate(email, password);

        // Assert
        assertNotNull(response);
        assertNull(response.getAccessToken());

        verify(hasher, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyLong());
        verify(refreshRepo, never()).saveAndFlush(any(RefreshToken.class));
    }

    @Test
    void authenticate_InvalidPassword_ReturnsEmptyResponse() {
        // Arrange
        String email = "test@hva.nl";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword";

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPasswordHash(encodedPassword);

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(hasher.matches(password, encodedPassword)).thenReturn(false);

        // Act
        AuthenticationResponse response = authService.authenticate(email, password);

        // Assert
        assertNotNull(response);
        assertNull(response.getAccessToken());

        verify(jwtService, never()).generateToken(anyLong());
        verify(refreshRepo, never()).saveAndFlush(any(RefreshToken.class));
    }
}