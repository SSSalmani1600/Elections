package nl.hva.election_backend.controller;

// Test voor de Account pagina (UserController)

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nl.hva.election_backend.dto.UpdateUserRequest;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.service.JwtService;
import nl.hva.election_backend.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    private UserController userController;

    private User testUser;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService, jwtService);

        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.nl");
        testUser.setPasswordHash("hashedpassword123");
    }

    // Test: gebruiker ophalen
    @Test
    @DisplayName("Gebruiker ophalen - succes")
    void getUser_Success() {
        when(userService.getUserById(USER_ID)).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userController.getUser(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    // Test: gebruiker niet gevonden
    @Test
    @DisplayName("Gebruiker ophalen - niet gevonden")
    void getUser_NotFound() {
        when(userService.getUserById(USER_ID)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUser(USER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Test: account updaten success
    @Test
    @DisplayName("Account updaten - succes")
    void updateUser_Success() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCurrentPassword("correctpassword");
        request.setUsername("nieuwenaam");

        when(userService.updateUser(eq(USER_ID), any(UpdateUserRequest.class))).thenReturn(testUser);

        ResponseEntity<?> response = userController.updateUser(USER_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Test: fout wachtwoord
    @Test
    @DisplayName("Account updaten - fout wachtwoord")
    void updateUser_WrongPassword() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCurrentPassword("foutwachtwoord");

        when(userService.updateUser(eq(USER_ID), any(UpdateUserRequest.class)))
                .thenThrow(new SecurityException("Huidig wachtwoord is onjuist"));

        ResponseEntity<?> response = userController.updateUser(USER_ID, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Test: geen wachtwoord
    @Test
    @DisplayName("Account updaten - geen wachtwoord")
    void updateUser_NoPassword() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("nieuwenaam");

        when(userService.updateUser(eq(USER_ID), any(UpdateUserRequest.class)))
                .thenThrow(new IllegalArgumentException("Huidig wachtwoord is verplicht"));

        ResponseEntity<?> response = userController.updateUser(USER_ID, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Test: user niet gevonden
    @Test
    @DisplayName("Account updaten - niet gevonden")
    void updateUser_UserNotFound() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCurrentPassword("password");

        when(userService.updateUser(eq(USER_ID), any(UpdateUserRequest.class)))
                .thenThrow(new IllegalArgumentException("Gebruiker niet gevonden"));

        ResponseEntity<?> response = userController.updateUser(USER_ID, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Test: wachtwoord te kort
    @Test
    @DisplayName("Account updaten - wachtwoord te kort")
    void updateUser_PasswordTooShort() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCurrentPassword("correctpassword");
        request.setPassword("kort");

        when(userService.updateUser(eq(USER_ID), any(UpdateUserRequest.class)))
                .thenThrow(new IllegalArgumentException("Nieuw wachtwoord moet minimaal 8 karakters zijn"));

        ResponseEntity<?> response = userController.updateUser(USER_ID, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
