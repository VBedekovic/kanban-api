package hr.hivetech.Kanban.API.user;

import hr.hivetech.Kanban.API.password.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiUserServiceTest {

    @Mock
    private ApiUserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private ApiUserService apiUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_returnsUser_whenPasswordMatches() {
        ApiUser user = new ApiUser();
        user.setUsername("test");
        user.setPasswordHash("hash");
        when(userRepository.findById("test")).thenReturn(Optional.of(user));
        when(passwordService.verifyPassword("pw", "hash")).thenReturn(true);

        ApiUser result = apiUserService.authenticate("test", "pw");

        assertEquals(user, result);
    }

    @Test
    void authenticate_returnsNull_whenPasswordDoesNotMatch() {
        ApiUser user = new ApiUser();
        user.setUsername("test");
        user.setPasswordHash("hash");
        when(userRepository.findById("test")).thenReturn(Optional.of(user));
        when(passwordService.verifyPassword("pw", "hash")).thenReturn(false);

        ApiUser result = apiUserService.authenticate("test", "pw");

        assertNull(result);
    }

    @Test
    void authenticate_returnsNull_whenUserNotFound() {
        when(userRepository.findById("test")).thenReturn(Optional.empty());

        ApiUser result = apiUserService.authenticate("test", "pw");

        assertNull(result);
    }
}