package hr.hivetech.Kanban.API.task;

import hr.hivetech.Kanban.API.task.enums.TaskPriroirty;
import hr.hivetech.Kanban.API.task.enums.TaskStatus;
import hr.hivetech.Kanban.API.validators.ValidEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title is required")
    @NotBlank(message = "Title must not be empty")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description = "";

    @NotNull(message = "Status is required")
    @ValidEnum(enumClass = TaskStatus.class, message = "Status must be TO_DO, IN_PROGRESS or DONE")
    @Column(nullable = false)
    private String status;

    @NotNull(message = "Priority is required")
    @ValidEnum(enumClass = TaskPriroirty.class, message = "Priority must be LOW, MED or HIGH")
    @Column(nullable = false)
    private String priority;

    @Version
    private Long version;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = (description == null) ? "" : description;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }

}
