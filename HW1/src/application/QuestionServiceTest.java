
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import entity.*;

public class QuestionServiceTest {
    @Test
    void testCreateQuestion_StudentRole_Success() {
        User student = new User(1L, "Alice", User.UserRole.STUDENT);
        QuestionService service = new QuestionService();
        Question q = service.createQuestion("Q101", "How to debug?", student);
        assertNotNull(q.getNumber());
        assertEquals(Question.Status.UNRESOLVED, q.getStatus());
    }

    @Test
    void testCreateQuestion_AdminRole_ThrowsException() {
        User admin = new User(2L, "Bob", User.UserRole.ADMIN);
        QuestionService service = new QuestionService();
        assertThrows(SecurityException.class,
            () -> service.createQuestion("Q102", "Invalid", admin));
    }
}
