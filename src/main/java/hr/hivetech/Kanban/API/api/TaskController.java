package hr.hivetech.Kanban.API.api;

import hr.hivetech.Kanban.API.task.Task;
import hr.hivetech.Kanban.API.task.TaskService;
import hr.hivetech.Kanban.API.task.enums.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Page<Task>> getTasksPage(
            @RequestParam(required = true) TaskStatus status,
            @RequestParam(required = true) int page,
            @RequestParam(required = true) int size,
            @RequestParam(defaultValue = "id") String[] sort
    ) {
        Sort sortObj = Sort.by(sort);
        PageRequest pageRequest = PageRequest.of(page, size, sortObj);
        Page<Task> tasks = taskService.getTasksPage(status, pageRequest);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("({id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task savedTask = taskService.uploadTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateWholeTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        try {
            Task task = taskService.updateWholeTask(id, updatedTask);
            return ResponseEntity.ok(task);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PatchMapping(
            value = "/{id}",
            consumes = "application/merge-patch+json",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Task> patchTask(
            @PathVariable Long id,
            @RequestBody String mergePatchJson
    ) throws Exception {
        try {
            Task patchedTask = taskService.patchTask(id, mergePatchJson);
            return ResponseEntity.ok(patchedTask);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

}
