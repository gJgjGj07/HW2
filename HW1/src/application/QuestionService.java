
package service;

import entity.Question;
import entity.User;
import repository.QuestionRepository;
import java.util.List;

public class QuestionService {
    private QuestionRepository questionRepository;

    public Question createQuestion(String number, String desc, User creator) {
        if (!creator.hasRole(User.UserRole.STUDENT)) {
            throw new SecurityException("Only students can create questions!");
        }
        Question question = new Question(number, desc);
        return questionRepository.save(question);
    }

    public int batchMarkResolved(List<Long> questionIds, User admin) {
        if (!admin.hasRole(User.UserRole.ADMIN)) {
            throw new SecurityException("Requires ADMIN role!");
        }
        List<Question> questions = questionRepository.findAllById(questionIds);
        int count = 0;
        for (Question q : questions) {
            q.markResolved();
            questionRepository.save(q);
            count++;
        }
        return count;
    }
    
    public interface QuestionRepository {
        Question save(Question q);
        List<Question> findAllById(List<Long> ids);
    }
    public QuestionService(QuestionRepository repo) {
        this.questionRepository = repo;
    }
    public BatchResult batchMarkResolved(List<Long> ids, User admin) {
        List<Long> failedIds = new ArrayList<>();
        for (Long id : ids) {
            try {
                Question q = repository.findById(id);
                q.markResolved();
            } catch (Exception e) {
                failedIds.add(id);
            }
        }
        return new BatchResult(ids.size()-failedIds.size(), failedIds);
    }
}