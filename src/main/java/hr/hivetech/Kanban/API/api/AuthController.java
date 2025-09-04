package hr.hivetech.Kanban.API.api;

import hr.hivetech.Kanban.API.jwt.JwtService;
import hr.hivetech.Kanban.API.user.ApiUser;
import hr.hivetech.Kanban.API.user.ApiUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final ApiUserService userService;
    private final JwtService jwtService;

    public AuthController(ApiUserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest) {
        ApiUser user = userService.authenticate(loginRequest.username(), loginRequest.password());
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public record LoginRequest(String username, String password) {}
    public record JwtResponse(String token) {}
}
