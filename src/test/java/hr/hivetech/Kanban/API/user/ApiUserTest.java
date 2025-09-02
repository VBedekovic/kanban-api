package hr.hivetech.Kanban.API.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiUserTest {

    @Test
    void testUsernameGetterSetter() {
        ApiUser user = new ApiUser();
        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void testPasswordHashGetterSetter() {
        ApiUser user = new ApiUser();
        user.setPasswordHash("hashed");
        assertEquals("hashed", user.getPasswordHash());
    }
}
