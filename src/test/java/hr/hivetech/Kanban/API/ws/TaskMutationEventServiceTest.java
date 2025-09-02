package hr.hivetech.Kanban.API.ws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TaskMutationEventServiceTest {

    private SimpMessagingTemplate messagingTemplate;
    private TaskMutationEventService service;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        service = new TaskMutationEventService(messagingTemplate);
    }

    @Test
    void publishTaskMutationEvent_sendsMessageWithSuffix() {
        String action = "CREATE";
        Object payload = new Object();

        service.publishTaskMutationEvent(action, payload);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/tasks"), eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertTrue(event instanceof TaskEvent);
        TaskEvent taskEvent = (TaskEvent) event;
        assertEquals("CREATE TASK", taskEvent.action());
        assertEquals(payload, taskEvent.payload());
    }
}