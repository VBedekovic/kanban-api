package hr.hivetech.Kanban.API.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secret = "mytestsecretkey12345678901234567890";
    private final long expiration = 100000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        try {
            var secretField = JwtService.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(jwtService, secret);

            var expirationField = JwtService.class.getDeclaredField("expirationTime");
            expirationField.setAccessible(true);
            expirationField.set(jwtService, expiration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        jwtService.init();
    }

    @Test
    void generateToken_and_getUsernameFromToken_work() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        assertNotNull(token);

        String extracted = jwtService.getUsernameFromToken(token);
        assertEquals(username, extracted);
    }

    @Test
    void validateToken_returnsTrue_forValidToken() {
        String token = jwtService.generateToken("user");
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void validateToken_returnsFalse_forInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertFalse(jwtService.validateToken(invalidToken));
    }

    @Test
    void getUsernameFromToken_returnsCorrectUsername() {
        String username = "anotheruser";
        String token = jwtService.generateToken(username);

        String result = jwtService.getUsernameFromToken(token);
        assertEquals(username, result);
    }

    @Test
    void getClaims_throwsException_forInvalidToken() {
        assertThrows(Exception.class, () -> jwtService.getUsernameFromToken("bad.token"));
    }
}
