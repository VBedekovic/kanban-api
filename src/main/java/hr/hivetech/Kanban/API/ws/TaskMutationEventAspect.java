package hr.hivetech.Kanban.API.ws;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TaskMutationEventAspect {
    private final TaskMutationEventService taskMutationEventService;
    public TaskMutationEventAspect(TaskMutationEventService taskMutationEventService) {
        this.taskMutationEventService = taskMutationEventService;
    }

    @AfterReturning(value = "@annotation(hr.hivetech.Kanban.API.ws.PublishTaskEvent)", returning = "result")
    public void afterMutation(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        PublishTaskEvent annotation = signature.getMethod().getAnnotation(PublishTaskEvent.class);

        if (annotation != null) {
            taskMutationEventService.publishTaskMutationEvent(annotation.action(), result);
        }
    }
}
