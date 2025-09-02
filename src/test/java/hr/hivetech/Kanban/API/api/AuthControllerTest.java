package hr.hivetech.Kanban.API.api;

import hr.hivetech.Kanban.API.jwt.JwtService;
import hr.hivetech.Kanban.API.user.ApiUser;
import hr.hivetech.Kanban.API.user.ApiUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private ApiUserService userService;

    @Mock
    private JwtService jwtUtil;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_returnsJwtResponse_whenCredentialsValid() {
        ApiUser user = new ApiUser();
        user.setUsername("user");
        AuthController.LoginRequest req = new AuthController.LoginRequest("user", "pw");
        when(userService.authenticate("user", "pw")).thenReturn(user);
        when(jwtUtil.generateToken("user")).thenReturn("token123");

        ResponseEntity<?> response = controller.authenticate(req);

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(AuthController.JwtResponse.class, response.getBody());
        assertEquals("token123", ((AuthController.JwtResponse) response.getBody()).token());
    }

    @Test
    void authenticate_returns401_whenCredentialsInvalid() {
        AuthController.LoginRequest req = new AuthController.LoginRequest("user", "pw");
        when(userService.authenticate("user", "pw")).thenReturn(null);

        ResponseEntity<?> response = controller.authenticate(req);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody());
    }
}