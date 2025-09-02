package hr.hivetech.Kanban.API.task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testGettersAndSetters() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Title");
        task.setDescription("Test Description");
        task.setStatus("TO_DO");
        task.setPriority("HIGH");
        task.setVersion(2L);

        assertEquals(1L, task.getId());
        assertEquals("Test Title", task.getTitle());
        assertEquals("Test Description", task.getDescription());
        assertEquals("TO_DO", task.getStatus());
        assertEquals("HIGH", task.getPriority());
        assertEquals(2L, task.getVersion());
    }

    @Test
    void testDescriptionNullDefaultsToEmptyString() {
        Task task = new Task();
        task.setDescription(null);
        assertEquals("", task.getDescription());
    }
}
