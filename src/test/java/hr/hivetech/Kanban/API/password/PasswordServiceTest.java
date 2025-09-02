package hr.hivetech.Kanban.API.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordServiceTest {

    @Mock
    private BCryptPasswordEncoder encoder;

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordService = new PasswordService() {
            private final BCryptPasswordEncoder mockEncoder = encoder;
            @Override
            public String hashPassword(String rawPassword) {
                return mockEncoder.encode(rawPassword);
            }
            @Override
            public boolean verifyPassword(String rawPassword, String storedHash) {
                return mockEncoder.matches(rawPassword, storedHash);
            }
        };
    }

    @Test
    void hashPassword_returnsEncodedPassword() {
        String rawPassword = "secret";
        String encoded = "$2a$10$abcdef";
        when(encoder.encode(rawPassword)).thenReturn(encoded);

        String result = passwordService.hashPassword(rawPassword);

        assertEquals(encoded, result);
        verify(encoder).encode(rawPassword);
    }

    @Test
    void verifyPassword_returnsTrue_whenPasswordMatches() {
        String rawPassword = "secret";
        String storedHash = "$2a$10$abcdef";
        when(encoder.matches(rawPassword, storedHash)).thenReturn(true);

        boolean result = passwordService.verifyPassword(rawPassword, storedHash);

        assertTrue(result);
        verify(encoder).matches(rawPassword, storedHash);
    }

    @Test
    void verifyPassword_returnsFalse_whenPasswordDoesNotMatch() {
        String rawPassword = "secret";
        String storedHash = "$2a$10$abcdef";
        when(encoder.matches(rawPassword, storedHash)).thenReturn(false);

        boolean result = passwordService.verifyPassword(rawPassword, storedHash);

        assertFalse(result);
        verify(encoder).matches(rawPassword, storedHash);
    }
}