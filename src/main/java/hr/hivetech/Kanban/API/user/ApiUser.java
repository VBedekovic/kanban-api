package hr.hivetech.Kanban.API.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ApiUser {
    @Id
    private String username;

    private String passwordHash;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
