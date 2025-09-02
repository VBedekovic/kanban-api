package hr.hivetech.Kanban.API.user;

import hr.hivetech.Kanban.API.password.PasswordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class DemoUserInitializer {

    @Value("${demo.user.username}")
    private String demoUsername;

    @Value("${demo.user.password}")
    private String demoPassword;

    private final ApiUserRepository userRepository;
    private final PasswordService passwordService;

    public DemoUserInitializer(ApiUserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @Bean
    public CommandLineRunner insertDemoUser() {
        return args -> {
            if (!userRepository.existsById(demoUsername)) {
                ApiUser demoUser = new ApiUser();
                demoUser.setUsername(demoUsername);
                demoUser.setPasswordHash(passwordService.hashPassword(demoPassword));
                userRepository.save(demoUser);
            }
        };
    }
}
