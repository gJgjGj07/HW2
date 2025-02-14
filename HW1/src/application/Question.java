
package entity;

import java.time.LocalDateTime;

public class Question {
    private Long id;
    private String number;
    private String description;
    private Status status;
    private LocalDateTime createdAt;
    public enum Status { UNRESOLVED, RESOLVED, DELETED }
    public void markResolved() { this.status = Status.RESOLVED; }

    public Question(String number, String description) {
        this.number = number;
        this.description = description;
        this.status = Status.UNRESOLVED;
        this.createdAt = LocalDateTime.now();
    }

    public void markResolved() {
        this.status = Status.RESOLVED;
    }

    public boolean updateDescription(String newDescription) {
        if (newDescription == null || newDescription.isEmpty()) {
            return false;
        }
        this.description = newDescription;
        return true;
    }

    public enum Status {
        UNRESOLVED, RESOLVED, DELETED
    }

}
