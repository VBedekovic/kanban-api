package hr.hivetech.Kanban.API.task;

import hr.hivetech.Kanban.API.task.enums.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<Task> getTasksPage(TaskStatus status, Pageable pageable) {
        if (status == null) {
            return taskRepository.findAll(pageable);
        }
        return taskRepository.findByStatus(status, pageable);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task uploadTask(Task task) {
        return taskRepository.save(task);
    }

    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        taskRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Task updateWholeTask(Long id, Task updatedTask) {
        updatedTask.setId(id);
        return taskRepository.save(updatedTask);
    }

}
