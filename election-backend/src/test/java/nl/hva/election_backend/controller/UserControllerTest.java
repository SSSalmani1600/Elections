package nl.hva.election_backend.controller;

// Test voor de Account pagina (UserController)

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nl.hva.election_backend.dto.UpdateUserRequest;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.repository.TestRepository;
import nl.hva.election_backend.security.BCryptPasswordHasher;
import nl.hva.election_backend.service.JwtService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private TestRepository userRepository;

    @Mock
    private DiscussionRepository discussionRepository;

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private BCryptPasswordHasher passwordHasher;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.nl");
        testUser.setPasswordHash("hashedpassword123");
    }

    // Test: gebruiker ophalen met ID
    @Test
    @DisplayName("Gebruiker ophalen - succes")
    void getUser_Success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userController.getUser(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    // Test: gebruiker niet gevonden
    @Test
    @DisplayName("Gebruiker ophalen - niet gevonden")
    void getUser_NotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUser(USER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Test: account updaten met juist wachtwoord
    @Test
    @DisplayName("Account updaten - succes")
    void updateUser_Success() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCurrentPassword("correctpassword");
        request.setUsername("nieuwenaam");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordHasher.matches("correctpassword", "hashedpassword123")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = userController.updateUser(USER_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).save(any(User.class));
    }

    // Test: account updaten met fout wachtwoord
    @Test
    @DisplayName("Account updaten - fout wachtwoord")
    void updateUser_WrongPassword() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCurrentPassword("foutwachtwoord");
        request.setUsername("nieuwenaam");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordHasher.matches("foutwachtwoord", "hashedpassword123")).thenReturn(false);

        ResponseEntity<?> response = userController.updateUser(USER_ID, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    // Test: account updaten zonder wachtwoord
    @Test
    @DisplayName("Account updaten - geen wachtwoord meegegeven")
    void updateUser_NoPassword() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("nieuwenaam");
        // geen currentPassword gezet

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = userController.updateUser(USER_ID, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    // Test: account updaten - user bestaat niet
    @Test
    @DisplayName("Account updaten - gebruiker niet gevonden")
    void updateUser_UserNotFound() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCurrentPassword("password");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.updateUser(USER_ID, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Test: nieuw wachtwoord te kort
    @Test
    @DisplayName("Account updaten - nieuw wachtwoord te kort")
    void updateUser_PasswordTooShort() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCurrentPassword("correctpassword");
        request.setPassword("kort"); // minder dan 8 karakters

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordHasher.matches("correctpassword", "hashedpassword123")).thenReturn(true);

        ResponseEntity<?> response = userController.updateUser(USER_ID, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }
}


