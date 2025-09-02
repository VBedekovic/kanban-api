package hr.hivetech.Kanban.API.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskMutationEventService {
    private static final String ACTION_SUFFIX = " TASK";
    private final SimpMessagingTemplate messagingTemplate;
    public TaskMutationEventService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishTaskMutationEvent(String action, Object payload) {
        messagingTemplate.convertAndSend("/topic/tasks", new TaskEvent(action + ACTION_SUFFIX, payload));
    }
}
