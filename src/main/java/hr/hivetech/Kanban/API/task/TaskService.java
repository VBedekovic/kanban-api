package hr.hivetech.Kanban.API.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.hivetech.Kanban.API.task.enums.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static hr.hivetech.Kanban.API.utils.MergePatchUtil.applyMergePatch;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper;
    public TaskService(TaskRepository taskRepository, ObjectMapper objectMapper) {
        this.taskRepository = taskRepository;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "tasksPage", key = "{#status, #pageable.pageNumber, #pageable.pageSize, #pageable.sort}")
    public Page<Task> getTasksPage(TaskStatus status, Pageable pageable) {
        if (status == null) {
            return taskRepository.findAll(pageable);
        }
        return taskRepository.findByStatus(status.toString(), pageable);
    }

    @Cacheable(value = "taskById", key = "#id")
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @CacheEvict(value = {"tasksPage", "taskById"}, allEntries = true)
    public Task uploadTask(Task task) {
        return taskRepository.save(task);
    }

    @CacheEvict(value = {"tasksPage", "taskById"}, allEntries = true)
    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        taskRepository.deleteById(id);
        return true;
    }

    @CacheEvict(value = {"tasksPage", "taskById"}, allEntries = true)
    @Transactional
    public Task updateWholeTask(Long id, Task updatedTask) {
        updatedTask.setId(id);
        return taskRepository.save(updatedTask);
    }

    @CacheEvict(value = {"tasksPage", "taskById"}, allEntries = true)
    public Task patchTask(Long id, String mergePatchJson) throws Exception {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + id));

        JsonNode taskNode = objectMapper.valueToTree(existingTask);
        JsonNode patchNode = objectMapper.readTree(mergePatchJson);
        JsonNode patchedNode = applyMergePatch(taskNode, patchNode);

        Task patchedTask = objectMapper.treeToValue(patchedNode, Task.class);
        return taskRepository.save(patchedTask);
    }

}
