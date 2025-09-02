package hr.hivetech.Kanban.API.user;

import hr.hivetech.Kanban.API.password.PasswordService;
import org.springframework.stereotype.Service;

@Service
public class ApiUserService {
    private final ApiUserRepository userRepository;
    private final PasswordService passwordService;

    public ApiUserService(ApiUserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public ApiUser authenticate(String username, String password) {
        return userRepository.findById(username)
                .filter(user -> passwordService.verifyPassword(password, user.getPasswordHash()))
                .orElse(null);
    }

}
