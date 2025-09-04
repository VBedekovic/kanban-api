package hr.hivetech.Kanban.API.integration;

import hr.hivetech.Kanban.API.task.Task;
import hr.hivetech.Kanban.API.task.enums.TaskPriroirty;
import hr.hivetech.Kanban.API.task.enums.TaskStatus;
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
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TaskIntegrationTest {

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
    private TestRestTemplate restTemplate;

    private String getJwtToken() {
        var loginReq = new hr.hivetech.Kanban.API.api.AuthController.LoginRequest("postman", "randompassword");
        var resp = restTemplate.postForEntity("http://localhost:" + port + "/auth", loginReq, hr.hivetech.Kanban.API.api.AuthController.JwtResponse.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        return resp.getBody().token();
    }

    @Test
    void taskCrudFlow_works() throws Exception {
        String jwt = getJwtToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);

        // Create Task
        Task newTask = new Task();
        newTask.setTitle("Integration Test Task");
        newTask.setDescription("Test Description");
        newTask.setStatus(TaskStatus.TO_DO.name());
        newTask.setPriority(TaskPriroirty.HIGH.name());

        HttpEntity<Task> createReq = new HttpEntity<>(newTask, headers);
        ResponseEntity<Task> createResp = restTemplate.postForEntity("http://localhost:" + port + "/api/tasks", createReq, Task.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Task created = createResp.getBody();
        assertThat(created).isNotNull();
        Long taskId = created.getId();

        // Get Task
        ResponseEntity<Task> getResp = restTemplate.exchange("http://localhost:" + port + "/api/tasks/" + taskId, HttpMethod.GET, new HttpEntity<>(headers), Task.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody().getTitle()).isEqualTo("Integration Test Task");

        // Update Task
        created.setTitle("Updated Title");
        HttpEntity<Task> updateReq = new HttpEntity<>(created, headers);
        ResponseEntity<Task> updateResp = restTemplate.exchange("http://localhost:" + port + "/api/tasks/" + taskId, HttpMethod.PUT, updateReq, Task.class);
        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResp.getBody().getTitle()).isEqualTo("Updated Title");

        // Patch Task
        String patchJson = "{\"description\":\"Patched Description\"}";
        HttpHeaders patchHeaders = new HttpHeaders();
        patchHeaders.setBearerAuth(jwt);
        patchHeaders.setContentType(MediaType.valueOf("application/merge-patch+json"));
        HttpEntity<String> patchReq = new HttpEntity<>(patchJson, patchHeaders);
        ResponseEntity<Task> patchResp = restTemplate.exchange("http://localhost:" + port + "/api/tasks/" + taskId, HttpMethod.PATCH, patchReq, Task.class);
        assertThat(patchResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(patchResp.getBody().getDescription()).isEqualTo("Patched Description");

        // Delete Task
        ResponseEntity<Void> deleteResp = restTemplate.exchange("http://localhost:" + port + "/api/tasks/" + taskId, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Confirm Deletion
        ResponseEntity<Task> getAfterDeleteResp = restTemplate.exchange("http://localhost:" + port + "/api/tasks/" + taskId, HttpMethod.GET, new HttpEntity<>(headers), Task.class);
        assertThat(getAfterDeleteResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}