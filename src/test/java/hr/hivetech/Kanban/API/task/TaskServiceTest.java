package hr.hivetech.Kanban.API.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.hivetech.Kanban.API.task.enums.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTasksPage_returnsAllTasks_whenStatusIsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(new Task()));
        when(taskRepository.findAll(pageable)).thenReturn(page);

        Page<Task> result = taskService.getTasksPage(null, pageable);

        assertEquals(page, result);
        verify(taskRepository).findAll(pageable);
    }

    @Test
    void getTasksPage_returnsFilteredTasks_whenStatusIsProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(new Task()));
        when(taskRepository.findByStatus("TO_DO", pageable)).thenReturn(page);

        Page<Task> result = taskService.getTasksPage(TaskStatus.TO_DO, pageable);

        assertEquals(page, result);
        verify(taskRepository).findByStatus("TO_DO", pageable);
    }

    @Test
    void getTaskById_returnsTask_whenFound() {
        Task task = new Task();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> result = taskService.getTaskById(1L);

        assertTrue(result.isPresent());
        assertEquals(task, result.get());
    }

    @Test
    void uploadTask_savesTask() {
        Task task = new Task();
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.uploadTask(task);

        assertEquals(task, result);
        verify(taskRepository).save(task);
    }

    @Test
    void deleteTask_deletesAndReturnsTrue_whenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        boolean result = taskService.deleteTask(1L);

        assertTrue(result);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_returnsFalse_whenNotExists() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        boolean result = taskService.deleteTask(1L);

        assertFalse(result);
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateWholeTask_setsIdAndSaves() {
        Task updatedTask = new Task();
        when(taskRepository.save(updatedTask)).thenReturn(updatedTask);

        Task result = taskService.updateWholeTask(1L, updatedTask);

        assertEquals(updatedTask, result);
        assertEquals(1L, updatedTask.getId());
        verify(taskRepository).save(updatedTask);
    }

    @Test
    void patchTask_throwsException_whenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.patchTask(1L, "{}"));
    }
}
