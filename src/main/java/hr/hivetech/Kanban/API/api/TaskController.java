package hr.hivetech.Kanban.API.api;

import hr.hivetech.Kanban.API.task.Task;
import hr.hivetech.Kanban.API.task.TaskService;
import hr.hivetech.Kanban.API.task.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public Page<Task> getTasksPage(
            @RequestParam(required = true) TaskStatus status,
            @RequestParam(required = true) int page,
            @RequestParam(required = true) int size,
            @RequestParam(defaultValue = "id") String[] sort
    ) {
        Sort sortObj = Sort.by(sort);
        PageRequest pageRequest = PageRequest.of(page, size, sortObj);
        return taskService.getTasksPage(status, pageRequest);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.uploadTask(task);
    }
}
