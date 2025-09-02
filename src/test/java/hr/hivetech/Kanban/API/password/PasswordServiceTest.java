package hr.hivetech.Kanban.API.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
    }

    @Test
    void hashPassword_returnsEncodedPassword() {
        String rawPassword = "secret";
        String encoded = passwordService.hashPassword(rawPassword);

        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);
        assertTrue(encoded.startsWith("$2a$"));
    }

    @Test
    void verifyPassword_returnsTrue_whenPasswordMatches() {
        String rawPassword = "secret";
        String encoded = passwordService.hashPassword(rawPassword);

        assertTrue(passwordService.verifyPassword(rawPassword, encoded));
    }

    @Test
    void verifyPassword_returnsFalse_whenPasswordDoesNotMatch() {
        String rawPassword = "secret";
        String encoded = passwordService.hashPassword(rawPassword);

        assertFalse(passwordService.verifyPassword("wrongpassword", encoded));
    }
}