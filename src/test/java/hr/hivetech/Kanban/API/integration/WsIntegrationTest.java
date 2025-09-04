package hr.hivetech.Kanban.API.integration;
import hr.hivetech.Kanban.API.task.Task;
import hr.hivetech.Kanban.API.task.enums.TaskPriroirty;
import hr.hivetech.Kanban.API.task.enums.TaskStatus;
import hr.hivetech.Kanban.API.api.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class WsIntegrationTest {

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
        var loginReq = new AuthController.LoginRequest("postman", "randompassword");
        var resp = restTemplate.postForEntity("http://localhost:" + port + "/auth", loginReq, AuthController.JwtResponse.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        return resp.getBody().token();
    }

    @Test
    void broadcastAfterTaskPost_isReceivedByStompClient() throws Exception {
        String jwt = getJwtToken();

        // STOMP client
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        String wsUrl = "ws://localhost:" + port + "/ws";

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();

        StompHeaders connectHeaders = new StompHeaders();

        CompletableFuture<StompSession> sessionFuture =
                stompClient.connectAsync(wsUrl, handshakeHeaders, connectHeaders, new StompSessionHandlerAdapter() {});

        StompSession session = sessionFuture.get(5, TimeUnit.SECONDS);

        // Subscribe and await event
        CompletableFuture<Object> eventFuture = new CompletableFuture<>();
        session.subscribe("/topic/tasks", new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders headers) { return Object.class; }
            @Override public void handleFrame(StompHeaders headers, Object payload) { eventFuture.complete(payload); }
        });

        // POST a new task (auth via HTTP Bearer)
        Task newTask = new Task();
        newTask.setTitle("Broadcast Test Task");
        newTask.setDescription("WebSocket broadcast test");
        newTask.setStatus(TaskStatus.TO_DO.name());
        newTask.setPriority(TaskPriroirty.HIGH.name());

        var headers = new org.springframework.http.HttpHeaders();
        headers.setBearerAuth(jwt);
        var createReq = new org.springframework.http.HttpEntity<>(newTask, headers);
        restTemplate.postForEntity("http://localhost:" + port + "/api/tasks", createReq, Task.class);

        // Assert broadcast
        Object event = eventFuture.get(5, TimeUnit.SECONDS);
        assertThat(event).isNotNull();
    }
}