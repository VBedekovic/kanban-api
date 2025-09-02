package hr.hivetech.Kanban.API.api;

import hr.hivetech.Kanban.API.task.Task;
import hr.hivetech.Kanban.API.task.TaskService;
import hr.hivetech.Kanban.API.task.enums.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTasksPage_returnsPage() {
        Page<Task> page = new PageImpl<>(List.of(new Task()));
        when(taskService.getTasksPage(TaskStatus.TO_DO, PageRequest.of(0, 10, Sort.by("id")))).thenReturn(page);

        ResponseEntity<Page<Task>> response = controller.getTasksPage(TaskStatus.TO_DO, 0, 10, new String[]{"id"});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
    }

    @Test
    void getTaskById_returnsTask_whenFound() {
        Task task = new Task();
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

        ResponseEntity<Task> response = controller.getTaskById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
    }

    @Test
    void getTaskById_returnsNotFound_whenMissing() {
        when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Task> response = controller.getTaskById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createTask_returnsCreated() {
        Task task = new Task();
        when(taskService.uploadTask(task)).thenReturn(task);

        ResponseEntity<Task> response = controller.createTask(task);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(task, response.getBody());
    }

    @Test
    void deleteTask_returnsNoContent_whenDeleted() {
        when(taskService.deleteTask(1L)).thenReturn(true);

        ResponseEntity<Map<String, Long>> response = controller.deleteTask(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(Map.of("id", 1L), response.getBody());
    }

    @Test
    void deleteTask_returnsNotFound_whenNotDeleted() {
        when(taskService.deleteTask(1L)).thenReturn(false);

        ResponseEntity<Map<String, Long>> response = controller.deleteTask(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateWholeTask_returnsOk_whenUpdated() {
        Task updated = new Task();
        when(taskService.updateWholeTask(1L, updated)).thenReturn(updated);

        ResponseEntity<Task> response = controller.updateWholeTask(1L, updated);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    void updateWholeTask_returnsNotFound_whenEntityNotFound() {
        Task updated = new Task();
        when(taskService.updateWholeTask(1L, updated)).thenThrow(new EntityNotFoundException());

        ResponseEntity<Task> response = controller.updateWholeTask(1L, updated);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateWholeTask_returnsConflict_whenOptimisticLockingFailure() {
        Task updated = new Task();
        when(taskService.updateWholeTask(1L, updated)).thenThrow(new ObjectOptimisticLockingFailureException(Task.class, 1L));

        ResponseEntity<Task> response = controller.updateWholeTask(1L, updated);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void patchTask_returnsOk_whenPatched() throws Exception {
        Task patched = new Task();
        when(taskService.patchTask(1L, "{}")).thenReturn(patched);

        ResponseEntity<Task> response = controller.patchTask(1L, "{}");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patched, response.getBody());
    }

    @Test
    void patchTask_returnsNotFound_whenEntityNotFound() throws Exception {
        when(taskService.patchTask(1L, "{}")).thenThrow(new EntityNotFoundException());

        ResponseEntity<Task> response = controller.patchTask(1L, "{}");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}