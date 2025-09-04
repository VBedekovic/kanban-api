package hr.hivetech.Kanban.API.integration;

import hr.hivetech.Kanban.API.api.AuthController;
import hr.hivetech.Kanban.API.user.ApiUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("kanban")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("demo.user.username", () -> "postman");
        registry.add("demo.user.password", () -> "randompassword");
        registry.add("security.jwt.secret", () -> "my-very-secret-key-will-be-here");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private ApiUserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void authenticateDemoUser_returnsJwtToken() {
        assertThat(userRepository.existsById("postman")).isTrue();

        var loginRequest = new AuthController.LoginRequest("postman", "randompassword");
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/auth",
                loginRequest,
                AuthController.JwtResponse.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();
    }
}
