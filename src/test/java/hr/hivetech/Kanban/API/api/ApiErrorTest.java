package hr.hivetech.Kanban.API.api;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorTest {

    @Test
    void testDefaultConstructorSetsTimestamp() {
        ApiError error = new ApiError();
        assertNotNull(error.getTimestamp());
    }

    @Test
    void testParameterizedConstructorSetsFields() {
        ApiError error = new ApiError(400, "Bad Request", "Validation failed");
        assertEquals(400, error.getStatus());
        assertEquals("Bad Request", error.getError());
        assertEquals("Validation failed", error.getMessage());
        assertNotNull(error.getTimestamp());
    }

    @Test
    void testSettersAndGetters() {
        ApiError error = new ApiError();
        LocalDateTime now = LocalDateTime.now();
        error.setTimestamp(now);
        error.setStatus(500);
        error.setError("Internal Error");
        error.setMessage("Something went wrong");

        assertEquals(now, error.getTimestamp());
        assertEquals(500, error.getStatus());
        assertEquals("Internal Error", error.getError());
        assertEquals("Something went wrong", error.getMessage());
    }
}